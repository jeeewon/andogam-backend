package org.smwu.andogam.Report.service;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Report.domain.entity.Report;
import org.smwu.andogam.Report.domain.repository.ReportRepository;
import org.smwu.andogam.Report.dto.ReportListDto;
import org.smwu.andogam.Report.dto.ReportResponseDto;
import org.smwu.andogam.Report.dto.ReportSaveDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final ReportRepository reportRepository;

    //신고 등록
    public ReportSaveDto reportSave(String stationCode, ReportSaveDto reportSaveDto){
        reportSaveDto.setStationCode(stationCode);
        Report report = reportSaveDto.toEntity();
        reportRepository.save(report);

        return reportSaveDto;
    }

    //신고 리스트 불러오기
    public List<ReportListDto> reportList(String stationCode) {
        return reportRepository.findByStation(stationCode).stream()
                .map(ReportListDto::new)
                .collect(Collectors.toList());
    }

    public ReportResponseDto reportResponse(String stationCode){
        List<ReportListDto> reportList = reportList(stationCode);
        Long reportCount = reportRepository.getCount(stationCode);

        return new ReportResponseDto(reportCount,reportList);
    }
}
