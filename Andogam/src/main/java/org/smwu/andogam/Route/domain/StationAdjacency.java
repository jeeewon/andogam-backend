package org.smwu.andogam.Route.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * ODsay subwayStationInfo API 응답 한 건을 그래프 탐색에 필요한 형태로 정리한 것.
 * - prevNodes / nextNodes : 같은 노선에서 인접한 역 (같은 노선 이동 엣지)
 * - transferNodes         : 같은 물리적 역에서 환승 가능한 다른 노선의 역 (환승 엣지)
 * - hasElevator/Lift/Ramp : 이 물리적 역(stationName 기준)의 교통약자 편의시설 설치 여부
 */
@Getter
public class StationAdjacency {

    private final SubwayNode self;
    private final List<SubwayNode> prevNodes;
    private final List<SubwayNode> nextNodes;
    private final List<SubwayNode> transferNodes;
    private final boolean hasElevator;
    private final boolean hasLift;
    private final boolean hasRamp;

    public StationAdjacency(SubwayNode self,
                             List<SubwayNode> prevNodes,
                             List<SubwayNode> nextNodes,
                             List<SubwayNode> transferNodes,
                             boolean hasElevator,
                             boolean hasLift,
                             boolean hasRamp) {
        this.self = self;
        this.prevNodes = prevNodes == null ? Collections.emptyList() : prevNodes;
        this.nextNodes = nextNodes == null ? Collections.emptyList() : nextNodes;
        this.transferNodes = transferNodes == null ? Collections.emptyList() : transferNodes;
        this.hasElevator = hasElevator;
        this.hasLift = hasLift;
        this.hasRamp = hasRamp;
    }

    /** 이 역에 휠체어로 오르내릴 수 있는 시설이 하나라도 있는지 */
    public boolean isWheelchairAccessible() {
        return hasElevator || hasLift || hasRamp;
    }
}
