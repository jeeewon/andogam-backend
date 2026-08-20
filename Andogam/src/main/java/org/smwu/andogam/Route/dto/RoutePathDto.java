package org.smwu.andogam.Route.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoutePathDto {
    private List<RouteStepDto> steps;
    private int totalStationCount;      // 총 정차역 수
    private int transferCount;          // 총 환승 횟수
    private int inaccessibleTransferCount; // 편의시설 없는 환승 횟수 (0이면 완전 접근 가능 경로)
}
