package org.smwu.andogam.Report.controller;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Report.dto.ReportResponseDto;
import org.smwu.andogam.Report.dto.ReportSaveDto;
import org.smwu.andogam.Report.service.ReportService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/stationInfo/{stationCode}/reportSave")
    public ReportSaveDto reportSave(@PathVariable String stationCode, @RequestBody ReportSaveDto reportSaveDto) {
        return reportService.reportSave(stationCode,reportSaveDto);
    }

    @GetMapping("/stationInfo/{stationCode}/reports")
    public ReportResponseDto reportResponse(@PathVariable String stationCode){
        return reportService.reportResponse(stationCode);
    }


}
