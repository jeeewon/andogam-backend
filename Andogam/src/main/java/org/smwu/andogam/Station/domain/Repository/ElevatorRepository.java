package org.smwu.andogam.Station.domain.Repository;

import org.smwu.andogam.Station.domain.Entity.Elevator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElevatorRepository extends JpaRepository<Elevator,Integer>{
    Boolean existsByStationNameAndLaneName(String stationName,String laneName);
    // 노선명이 정확히 일치하는 데이터가 없을 때(예: 코레일 구간처럼 표기 체계가 다른 경우) 대비한 fallback
    Boolean existsByStationName(String stationName);
}