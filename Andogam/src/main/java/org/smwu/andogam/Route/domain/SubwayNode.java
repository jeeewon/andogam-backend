package org.smwu.andogam.Route.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 지하철 노선도 상의 한 지점(노드)을 나타낸다.
 * ODsay는 환승역이라도 노선마다 별도의 stationID를 부여하기 때문에,
 * "같은 물리적 역"이라도 노선이 다르면 서로 다른 SubwayNode로 취급한다.
 * (환승은 ex 엣지로 별도 연결됨)
 */
@Getter
@EqualsAndHashCode(of = "stationId")
public class SubwayNode {

    private final String stationId;   // ODsay stationID (=Station.stationCode)
    private final String stationName; // 물리적 역 이름 (환승 판단 및 접근성 조회에 사용)
    private final String laneName;    // 노선명 (ex: "수도권 2호선")

    public SubwayNode(String stationId, String stationName, String laneName) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.laneName = laneName;
    }

    @Override
    public String toString() {
        return stationName + "(" + laneName + ")";
    }
}
