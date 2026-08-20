package org.smwu.andogam.Route.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteResponseDto {
    private RoutePathDto normalPath;       // 최소 환승/최단 기준 경로
    private RoutePathDto accessiblePath;    // 교통약자 편의시설 우선 경로
    private boolean fullyAccessibleFound;   // accessiblePath가 편의시설 없는 환승 없이 완주 가능한지

    // normalPath 상에서 최근 3일 내 불편 신고가 있었던 역의 stationId 목록.
    // 비어있지 않으면 프론트에서 "다른 경로 보시겠습니까?" 확인을 띄우고,
    // 사용자가 동의하면 이 목록을 그대로 avoidStationIds에 담아 /route를 다시 호출하면 된다.
    private List<String> reportedStationIdsOnNormalPath;

    public boolean isReportedIssueOnNormalPath() {
        return reportedStationIdsOnNormalPath != null && !reportedStationIdsOnNormalPath.isEmpty();
    }
}
