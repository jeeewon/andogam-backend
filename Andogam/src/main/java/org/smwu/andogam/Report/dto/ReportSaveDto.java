package org.smwu.andogam.Report.dto;

import lombok.Data;
import org.smwu.andogam.Report.domain.entity.Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ReportSaveDto {
    private Long reportIdx;
    private String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    private Integer discomfort;
    private String stationCode;

    public Report toEntity() {
        Report report = Report.builder()
                .reportIdx(this.reportIdx)
                .createdDate(this.createdDate)
                .discomfort(this.discomfort)
                .stationCode(this.stationCode)
                .build();
        return report;
    }
}
