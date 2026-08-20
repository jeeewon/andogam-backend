package org.smwu.andogam.Route.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class RouteRequestDto {
    private String startStationCode; // ODsay stationID (Station.stationCode와 동일 체계)
    private String endStationCode;

    // 사용자가 "이 역들은 피해서 가줘"라고 명시적으로 지정한 stationId 목록.
    // 1차 /route 응답에서 받은 reportedStationIdsOnNormalPath를 사용자가 "다른 경로 보기"에
    // 동의했을 때 그대로 여기 담아 다시 요청하면 된다. 지정 안 하면(null) 아무것도 회피하지 않는다.
    private List<String> avoidStationIds;

    public List<String> getAvoidStationIdsOrEmpty() {
        return avoidStationIds == null ? Collections.emptyList() : avoidStationIds;
    }
}
