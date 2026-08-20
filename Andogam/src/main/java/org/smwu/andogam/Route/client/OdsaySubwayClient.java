package org.smwu.andogam.Route.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.smwu.andogam.Route.domain.StationAdjacency;
import org.smwu.andogam.Route.domain.SubwayNode;
import org.smwu.andogam.Route.domain.TransferInfo;
import org.smwu.andogam.Station.domain.Repository.ElevatorRepository;
import org.smwu.andogam.Station.domain.Repository.LiftRepository;
import org.smwu.andogam.Station.domain.Repository.RampRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ODsay 지하철역 세부 정보 조회(subwayStationInfo) API를 호출해서
 * 그래프 탐색용 StationAdjacency로 변환한다.
 *
 * NOTE: 같은 역을 여러 번 호출하지 않도록 RouteService 쪽에서 캐시(cache)를 씌워서 사용해야 한다.
 * 이 클래스 자체는 단건 호출/파싱만 책임진다.
 */
@Component
@RequiredArgsConstructor
public class OdsaySubwayClient {

    @Value("${api-odsay-key}")
    private String odsayKey;

    private final ElevatorRepository elevatorRepository;
    private final LiftRepository liftRepository;
    private final RampRepository rampRepository;

    public StationAdjacency fetchAdjacency(String stationId) throws IOException {
        String urlInfo = "https://api.odsay.com/v1/api/subwayStationInfo?lang=0&stationID="
                + stationId + "&apiKey=" + odsayKey;

        URL url = new URL(urlInfo);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            conn.disconnect();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(sb.toString());
            checkOdsayError(root, stationId);
            JSONObject result = (JSONObject) root.get("result");

            String stationName = String.valueOf(result.get("stationName")).trim();
            String laneName = String.valueOf(result.get("laneName")).trim();
            SubwayNode self = new SubwayNode(stationId, stationName, laneName);

            List<SubwayNode> prevNodes = parseStationList(result.get("prevOBJ"));
            List<SubwayNode> nextNodes = parseStationList(result.get("nextOBJ"));
            List<SubwayNode> transferNodes = parseStationList(result.get("exOBJ"));

            boolean hasElevator = elevatorRepository.existsByStationNameAndLaneName(stationName, laneName)
                    || (allowStationNameFallback(laneName) && elevatorRepository.existsByStationName(stationName));
            boolean hasLift = liftRepository.existsByStationNameAndLaneName(stationName, laneName)
                    || (allowStationNameFallback(laneName) && liftRepository.existsByStationName(stationName));
            boolean hasRamp = rampRepository.existsByStationNameAndLaneName(stationName, laneName)
                    || (allowStationNameFallback(laneName) && rampRepository.existsByStationName(stationName));

            return new StationAdjacency(self, prevNodes, nextNodes, transferNodes,
                    hasElevator, hasLift, hasRamp);
        } catch (ParseException e) {
            throw new IOException("ODsay 응답 파싱 실패 (stationID=" + stationId + ")", e);
        }
    }

    /**
     * ODsay 지하철역 환승 정보 조회(subwayTransitInfo) API를 호출한다.
     * 한 역에 대해, 그 역에서 가능한 모든 (타고온 노선 -> 환승할 노선) 조합의
     * 빠른환승(칸/문 번호) 정보를 리스트로 반환한다.
     */
    @SuppressWarnings("unchecked")
    public List<TransferInfo> fetchTransferInfo(String stationId) throws IOException {
        String urlInfo = "https://api.odsay.com/v1/api/subwayTransitInfo?lang=0&stationID="
                + stationId + "&apiKey=" + odsayKey;

        URL url = new URL(urlInfo);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            conn.disconnect();
        }

        List<TransferInfo> transferInfos = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(sb.toString());
            checkOdsayError(root, stationId);
            JSONObject result = (JSONObject) root.get("result");
            if (result == null) {
                return transferInfos; // 환승 정보 자체가 없는 역(비환승역)일 수 있음
            }

            Object listRaw = result.get("transitTotalInfo");
            if (listRaw == null) {
                return transferInfos;
            }

            Gson gson = new Gson();
            List<Map<String, Object>> infoList = gson.fromJson(
                    listRaw.toString(),
                    new TypeToken<List<Map<String, Object>>>() {}.getType());

            for (Map<String, Object> info : infoList) {
                String takeLaneName = String.valueOf(info.get("takeLaneName")).trim();
                String exLaneName = String.valueOf(info.get("exLaneName")).trim();
                String fastTrainInfo = info.get("fastTrainInfo") == null
                        ? null : String.valueOf(info.get("fastTrainInfo")).trim();
                int fastTrain = toIntSafely(info.get("FastTrain"));
                int fastDoor = toIntSafely(info.get("FastFastDoor"));
                int fastTrainNum = toIntSafely(info.get("FastTrainNum"));

                transferInfos.add(new TransferInfo(takeLaneName, exLaneName, fastTrainInfo,
                        fastTrain, fastDoor, fastTrainNum));
            }
        } catch (ParseException e) {
            throw new IOException("ODsay 환승정보 파싱 실패 (stationID=" + stationId + ")", e);
        }
        return transferInfos;
    }

    private int toIntSafely(Object raw) {
        if (raw == null) return 0;
        if (raw instanceof Double) return ((Double) raw).intValue();
        try {
            return Integer.parseInt(String.valueOf(raw));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * ODsay는 잘못된 stationID 등으로 요청이 실패하면 "result" 대신 "error" 노드를 반환한다.
     * (예: {"error":{"code":"-8","msg":"..."}})
     * 이걸 미리 걸러서 명확한 예외로 바꿔준다.
     */
    private void checkOdsayError(JSONObject root, String stationId) {
        Object errorNode = root.get("error");
        if (errorNode != null) {
            JSONObject error = (JSONObject) errorNode;
            String code = String.valueOf(error.get("code"));
            String msg = String.valueOf(error.get("msg"));
            throw new org.smwu.andogam.Common.exception.InvalidStationException(
                    "존재하지 않거나 유효하지 않은 역 코드입니다 (stationID=" + stationId
                            + ", ODsay 오류코드=" + code + ", 메시지=" + msg + ")");
        }
        if (root.get("result") == null) {
            throw new org.smwu.andogam.Common.exception.InvalidStationException(
                    "역 정보를 찾을 수 없습니다 (stationID=" + stationId + ")");
        }
    }

    private static final java.util.regex.Pattern NUMBERED_SEOUL_METRO_LINE =
            java.util.regex.Pattern.compile("^수도권 [1-9]호선$");

    /**
     * "수도권 1호선" ~ "수도권 9호선"처럼 노선번호가 명확한 서울 지하철은
     * 물리적 역 단위 fallback을 허용하지 않는다. 같은 역이라도 노선(운영 주체)마다
     * 편의시설 설치 여부가 다를 수 있기 때문이다 (예: 김포공항역 - 5호선은 서울교통공사가
     * 관리해 엘리베이터 데이터가 있지만, 9호선은 별도 민간 운영사(서울시메트로9호선㈜) 관할이라
     * 우리가 가진 데이터가 전혀 없다. 이 경우 "9호선에도 있겠지"라고 넘겨짚으면 안 되고,
     * 데이터가 없다는 사실 그대로 반영해야 한다).
     *
     * 반대로 코레일 구간처럼 ODsay의 laneName("수도권 1호선" 등)과
     * 우리가 가진 원본 데이터의 노선명 표기(예: "경부선")가 다른 경우에는,
     * 정확한 노선명 매칭이 애초에 불가능하므로 물리적 역 단위 fallback을 허용한다.
     */
    private boolean allowStationNameFallback(String laneName) {
        return !NUMBERED_SEOUL_METRO_LINE.matcher(laneName).matches();
    }

    /**
     * prevOBJ / nextOBJ / exOBJ 는 모두 { "station": [ {...}, {...} ] } 형태이거나,
     * 해당 방향에 역이 없는 경우(종점 등) 필드 자체가 없을 수 있다.
     */
    @SuppressWarnings("unchecked")
    private List<SubwayNode> parseStationList(Object objNode) {
        List<SubwayNode> nodes = new ArrayList<>();
        if (objNode == null) {
            return nodes;
        }

        JSONObject objJson = (JSONObject) objNode;
        Object stationField = objJson.get("station");
        if (stationField == null) {
            return nodes;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> stationList = gson.fromJson(
                stationField.toString(),
                new TypeToken<List<Map<String, Object>>>() {}.getType());

        for (Map<String, Object> s : stationList) {
            // stationID가 double로 역직렬화되므로 정수 문자열로 안전 변환
            Object idRaw = s.get("stationID");
            String stationId = idRaw == null ? null
                    : String.valueOf(((Double) idRaw).longValue());
            String stationName = String.valueOf(s.get("stationName")).trim();
            String laneName = String.valueOf(s.get("laneName")).trim();

            if (stationId != null) {
                nodes.add(new SubwayNode(stationId, stationName, laneName));
            }
        }
        return nodes;
    }
}
