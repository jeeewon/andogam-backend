package org.smwu.andogam.Station.domain.Repository;

import org.smwu.andogam.Station.domain.Entity.Ramp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RampRepository extends JpaRepository<Ramp, Integer> {
    Boolean existsByStationNameAndLaneName(String stationName,String laneName);
    Boolean existsByStationName(String stationName);
}