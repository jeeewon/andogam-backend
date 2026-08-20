package org.smwu.andogam.Report.domain.repository;

import org.smwu.andogam.Report.domain.entity.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query(value = "SELECT * FROM report WHERE station_code=:stationCode ORDER BY report_idx DESC",nativeQuery = true)
    List<Report> findByStation(String stationCode);

    @Query(value = "SELECT count(*) FROM report WHERE station_code=:stationCode",nativeQuery = true)
    Long getCount(String stationCode);

    // created_date가 "yyyy.MM.dd" 형식(고정 자릿수)으로 저장돼 있어서 문자열 비교로도 날짜 비교가 정확히 된다.
    // MySQL은 진짜 불리언 타입이 없어서 "COUNT(*) > 0"을 Boolean으로 바로 매핑하면
    // JDBC가 BigInteger를 돌려줘서 ClassCastException이 난다. Long으로 받고 자바에서 0 초과 여부를 비교한다.
    @Query(value = "SELECT COUNT(*) FROM report WHERE station_code=:stationCode AND created_date >= :thresholdDate",
            nativeQuery = true)
    Long countRecentReport(String stationCode, String thresholdDate);
}
