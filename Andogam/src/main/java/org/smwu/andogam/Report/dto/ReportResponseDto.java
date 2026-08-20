package org.smwu.andogam.Report.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
public class ReportResponseDto {
    Long reportCount;
    List<ReportListDto> reportList;

    public ReportResponseDto(Long reportCount,List<ReportListDto> reportList){
        this.reportCount = reportCount;
        this.reportList = reportList;
    }
}
