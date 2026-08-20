package org.smwu.andogam.Route.service;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Report.domain.repository.ReportRepository;
import org.smwu.andogam.Route.client.OdsaySubwayClient;
import org.smwu.andogam.Route.domain.StationAdjacency;
import org.smwu.andogam.Route.domain.SubwayNode;
import org.smwu.andogam.Route.domain.TransferInfo;
import org.smwu.andogam.Route.dto.RoutePathDto;
import org.smwu.andogam.Route.dto.RouteRequestDto;
import org.smwu.andogam.Route.dto.RouteResponseDto;
import org.smwu.andogam.Route.dto.RouteStepDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final OdsaySubwayClient odsaySubwayClient;
    private final ReportRepository reportRepository;

    // 환승 기본 비용 (같은 노선 한 정거장 이동을 1로 두었을 때, 환승 자체의 기본 비용)
    private static final int TRANSFER_BASE_COST = 10;
    // 접근성 경로 계산 시, 환승 지점의 시설 상태에 따라 추가되는 페널티
    private static final int PENALTY_NO_FACILITY = 1000; // 사실상 회피 대상
    private static final int PENALTY_LIFT_ONLY = 15;      // 리프트만 있음 (직원 호출 등 번거로움)
    private static final int PENALTY_RAMP_ONLY = 5;       // 경사로만 있음 (비교적 양호)
    // 빠른환승(칸/문 정보가 명확한 환승) 데이터가 있을 때 접근성 경로 계산에 주는 보너스(비용 감소)
    private static final int FAST_TRANSFER_BONUS = 3;
    // 사용자가 명시적으로 "피해줘"라고 지정한 역(avoidStationIds)에 주는 페널티.
    // 편의시설 미설치와 동급으로 사실상 회피 대상으로 취급한다.
    private static final int PENALTY_USER_AVOID = 1000;
    // 그래프가 무한히 커지는 것을 막는 안전장치
    private static final int MAX_EXPANDED_NODES = 3000;
    // 신고를 "최근"으로 간주하는 기간
    private static final int RECENT_REPORT_DAYS = 3;

    public RouteResponseDto findRoute(RouteRequestDto request) throws IOException {
        Map<String, StationAdjacency> cache = new HashMap<>();
        Map<String, List<TransferInfo>> transferCache = new HashMap<>();
        Set<String> avoidStationIds = new HashSet<>(request.getAvoidStationIdsOrEmpty());

        List<SubwayNode> normalRoute = dijkstra(
                request.getStartStationCode(), request.getEndStationCode(), cache, transferCache,
                false, avoidStationIds);
        List<SubwayNode> accessibleRoute = dijkstra(
                request.getStartStationCode(), request.getEndStationCode(), cache, transferCache,
                true, avoidStationIds);

        RoutePathDto normalPath = toRoutePathDto(normalRoute, cache, transferCache);
        RoutePathDto accessiblePath = toRoutePathDto(accessibleRoute, cache, transferCache);

        return RouteResponseDto.builder()
                .normalPath(normalPath)
                .accessiblePath(accessiblePath)
                .fullyAccessibleFound(accessiblePath.getInaccessibleTransferCount() == 0)
                .reportedStationIdsOnNormalPath(findRecentReportedStationIds(normalRoute))
                .build();
    }

    /** normalPath에 포함된 역들 중 최근 N일 내 불편 신고가 있는 stationId 목록을 반환한다. */
    private List<String> findRecentReportedStationIds(List<SubwayNode> route) {
        String thresholdDate = LocalDate.now().minusDays(RECENT_REPORT_DAYS)
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        List<String> reported = new ArrayList<>();
        Set<String> checkedStationIds = new HashSet<>();
        for (SubwayNode node : route) {
            String stationId = node.getStationId();
            if (!checkedStationIds.add(stationId)) {
                continue; // 이미 확인한 역은 중복 조회하지 않음
            }
            Long recentCount = reportRepository.countRecentReport(stationId, thresholdDate);
            if (recentCount != null && recentCount > 0) {
                reported.add(stationId);
            }
        }
        return reported;
    }

    /**
     * @param accessibilityAware true면 환승 시 편의시설 유무 + 빠른환승 여부를 비용에 반영한다.
     * @param avoidStationIds    사용자가 명시적으로 피해달라고 지정한 stationId 집합. 빈 집합이면 아무 영향 없다.
     * @return 시작역부터 "도착역과 같은 물리적 역"에 처음 닿을 때까지의 SubwayNode 경로
     */
    private List<SubwayNode> dijkstra(String startStationId, String endStationId,
                                       Map<String, StationAdjacency> cache,
                                       Map<String, List<TransferInfo>> transferCache,
                                       boolean accessibilityAware,
                                       Set<String> avoidStationIds) throws IOException {

        String targetStationName = getAdjacency(endStationId, cache).getSelf().getStationName();

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> parent = new HashMap<>(); // stationId -> 바로 이전 stationId
        PriorityQueue<String[]> queue = new PriorityQueue<>(
                Comparator.comparingInt(a -> Integer.parseInt(a[1])));

        distance.put(startStationId, 0);
        parent.put(startStationId, null);
        queue.add(new String[]{startStationId, "0"});

        int expanded = 0;
        String reachedStationId = null;

        while (!queue.isEmpty()) {
            String[] current = queue.poll();
            String currentId = current[0];
            int currentDist = Integer.parseInt(current[1]);

            if (currentDist > distance.getOrDefault(currentId, Integer.MAX_VALUE)) {
                continue; // 이미 더 짧은 경로로 처리된 노드
            }

            StationAdjacency currentAdj = getAdjacency(currentId, cache);
            if (currentAdj.getSelf().getStationName().equals(targetStationName)) {
                reachedStationId = currentId;
                break;
            }

            if (++expanded > MAX_EXPANDED_NODES) {
                throw new IllegalStateException("탐색 범위를 초과했습니다 (start="
                        + startStationId + ", end=" + endStationId + ")");
            }

            relax(currentAdj.getNextNodes(), currentId, 1, currentDist, distance, parent, queue, avoidStationIds);
            relax(currentAdj.getPrevNodes(), currentId, 1, currentDist, distance, parent, queue, avoidStationIds);

            relaxTransfers(currentAdj, currentId, currentDist, distance, parent, queue,
                    transferCache, accessibilityAware, avoidStationIds);
        }

        if (reachedStationId == null) {
            throw new IllegalStateException(
                    "경로를 찾지 못했습니다 (start=" + startStationId + ", end=" + endStationId + ")");
        }

        return buildPath(reachedStationId, parent, cache);
    }

    private void relax(List<SubwayNode> neighbors, String currentId, int edgeCost, int currentDist,
                        Map<String, Integer> distance, Map<String, String> parent,
                        PriorityQueue<String[]> queue, Set<String> avoidStationIds) {
        for (SubwayNode neighbor : neighbors) {
            int cost = edgeCost + avoidPenalty(neighbor.getStationId(), avoidStationIds);
            int newDist = currentDist + cost;
            if (newDist < distance.getOrDefault(neighbor.getStationId(), Integer.MAX_VALUE)) {
                distance.put(neighbor.getStationId(), newDist);
                parent.put(neighbor.getStationId(), currentId);
                queue.add(new String[]{neighbor.getStationId(), String.valueOf(newDist)});
            }
        }
    }

    /** 환승 엣지는 편의시설 페널티 + 빠른환승 보너스가 대상마다(노선마다) 달라서 개별 계산이 필요하다. */
    private void relaxTransfers(StationAdjacency currentAdj, String currentId, int currentDist,
                                 Map<String, Integer> distance, Map<String, String> parent,
                                 PriorityQueue<String[]> queue,
                                 Map<String, List<TransferInfo>> transferCache,
                                 boolean accessibilityAware,
                                 Set<String> avoidStationIds) throws IOException {
        int penalty = accessibilityAware ? accessibilityPenalty(currentAdj) : 0;
        int baseCost = TRANSFER_BASE_COST + penalty;

        for (SubwayNode neighbor : currentAdj.getTransferNodes()) {
            int edgeCost = baseCost;

            // 빠른환승 보너스는 "이미 엘리베이터로 완전히 접근 가능한 환승"에 한해서만 적용한다.
            // ODsay의 빠른환승(칸/문) 정보는 일반 승객 기준 최단 도보 경로일 뿐, 그 경로가
            // 계단/에스컬레이터일 수도 있어 휠체어 이용자에게 실제로 유리한지 보장할 수 없다.
            // 따라서 접근성이 이미 확보된(penalty=0) 상태에서 "그중 더 빠른 쪽"을 고르는
            // 타이브레이커로만 쓰고, 접근성 자체를 대체하는 근거로는 쓰지 않는다.
            if (accessibilityAware && penalty == 0) {
                TransferInfo info = findTransferInfo(currentId, currentAdj.getSelf().getLaneName(),
                        neighbor.getLaneName(), transferCache);
                if (info != null && info.isDataAvailable()) {
                    edgeCost = Math.max(1, edgeCost - FAST_TRANSFER_BONUS);
                }
            }

            edgeCost += avoidPenalty(neighbor.getStationId(), avoidStationIds);

            int newDist = currentDist + edgeCost;
            if (newDist < distance.getOrDefault(neighbor.getStationId(), Integer.MAX_VALUE)) {
                distance.put(neighbor.getStationId(), newDist);
                parent.put(neighbor.getStationId(), currentId);
                queue.add(new String[]{neighbor.getStationId(), String.valueOf(newDist)});
            }
        }
    }

    /** 사용자가 명시적으로 피해달라고 요청한 역이면 무거운 페널티를, 아니면 0을 반환한다. */
    private int avoidPenalty(String stationId, Set<String> avoidStationIds) {
        return avoidStationIds.contains(stationId) ? PENALTY_USER_AVOID : 0;
    }

    private int accessibilityPenalty(StationAdjacency adjacency) {
        if (adjacency.isHasElevator()) {
            return 0;
        }
        if (adjacency.isHasRamp()) {
            return PENALTY_RAMP_ONLY;
        }
        if (adjacency.isHasLift()) {
            return PENALTY_LIFT_ONLY;
        }
        return PENALTY_NO_FACILITY;
    }

    private StationAdjacency getAdjacency(String stationId, Map<String, StationAdjacency> cache) throws IOException {
        StationAdjacency cached = cache.get(stationId);
        if (cached != null) {
            return cached;
        }
        StationAdjacency fetched = odsaySubwayClient.fetchAdjacency(stationId);
        cache.put(stationId, fetched);
        return fetched;
    }

    private List<TransferInfo> getTransferInfo(String stationId, Map<String, List<TransferInfo>> transferCache)
            throws IOException {
        List<TransferInfo> cached = transferCache.get(stationId);
        if (cached != null) {
            return cached;
        }
        List<TransferInfo> fetched = odsaySubwayClient.fetchTransferInfo(stationId);
        transferCache.put(stationId, fetched);
        return fetched;
    }

    /** stationId 에서 takeLaneName -> exLaneName 조합에 해당하는 빠른환승 정보를 찾는다. 없으면 null. */
    private TransferInfo findTransferInfo(String stationId, String takeLaneName, String exLaneName,
                                           Map<String, List<TransferInfo>> transferCache) throws IOException {
        String normalizedTake = normalizeLaneName(takeLaneName);
        String normalizedEx = normalizeLaneName(exLaneName);
        for (TransferInfo info : getTransferInfo(stationId, transferCache)) {
            if (normalizeLaneName(info.getTakeLaneName()).equals(normalizedTake)
                    && normalizeLaneName(info.getExLaneName()).equals(normalizedEx)) {
                return info;
            }
        }
        return null;
    }

    /**
     * ODsay의 subwayStationInfo는 laneName을 "수도권 2호선"처럼 "수도권 " 접두어를 붙여 주는 반면,
     * subwayTransitInfo(환승정보)는 같은 노선을 "2호선"처럼 접두어 없이 준다.
     * 두 API 결과를 서로 비교하려면 이 접두어 차이를 먼저 없애야 한다.
     */
    private String normalizeLaneName(String laneName) {
        if (laneName == null) return "";
        return laneName.replace("수도권 ", "").trim();
    }

    private List<SubwayNode> buildPath(String endId, Map<String, String> parent,
                                        Map<String, StationAdjacency> cache) {
        LinkedList<SubwayNode> path = new LinkedList<>();
        String cursor = endId;
        while (cursor != null) {
            path.addFirst(cache.get(cursor).getSelf());
            cursor = parent.get(cursor);
        }
        return path;
    }

    private RoutePathDto toRoutePathDto(List<SubwayNode> route, Map<String, StationAdjacency> cache,
                                         Map<String, List<TransferInfo>> transferCache) throws IOException {
        List<RouteStepDto> steps = new ArrayList<>();
        int transferCount = 0;
        int inaccessibleTransferCount = 0;

        for (int i = 0; i < route.size(); i++) {
            SubwayNode node = route.get(i);
            boolean isTransfer = i > 0 && !node.getLaneName().equals(route.get(i - 1).getLaneName());

            boolean transferAccessible = false;
            TransferInfo transferInfo = null;

            if (isTransfer) {
                transferCount++;
                String prevStationId = route.get(i - 1).getStationId();
                StationAdjacency prevAdj = cache.get(prevStationId);
                transferAccessible = prevAdj != null && prevAdj.isWheelchairAccessible();
                if (!transferAccessible) {
                    inaccessibleTransferCount++;
                }
                transferInfo = findTransferInfo(prevStationId, route.get(i - 1).getLaneName(),
                        node.getLaneName(), transferCache);
            }

            steps.add(RouteStepDto.of(node, isTransfer, transferAccessible, transferInfo));
        }

        return RoutePathDto.builder()
                .steps(steps)
                .totalStationCount(steps.size())
                .transferCount(transferCount)
                .inaccessibleTransferCount(inaccessibleTransferCount)
                .build();
    }
}
