package org.smwu.andogam.Search.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchDetailDto {
    private String type;
    private String stationId;   // ODsay stationID (지하철역 상세조회/경로탐색에 사용)
    private String stationName;
    private String ebid;
    private String address;

    @Builder
    public SearchDetailDto(String type, String stationId, String stationName, String ebid, String address) {
        this.type = type;
        this.stationId = stationId;
        this.stationName = stationName;
        this.ebid = ebid;
        this.address = address;
    }
}