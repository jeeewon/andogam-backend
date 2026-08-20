package org.smwu.andogam.Report.dto;

import lombok.Getter;
import org.smwu.andogam.Report.domain.entity.Report;

@Getter
public class ReportListDto {
    private Long reportIdx;
    private String createdDate;
    private Integer discomfort;

    public ReportListDto(Report report){
        this.reportIdx = report.getReportIdx();
        this.createdDate = report.getCreatedDate();
        this.discomfort = report.getDiscomfort();
    }
}
