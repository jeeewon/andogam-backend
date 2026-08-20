package org.smwu.andogam.Station.domain.Repository;

import org.smwu.andogam.Station.domain.Entity.Lift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiftRepository extends JpaRepository<Lift,Integer> {
    Boolean existsByStationNameAndLaneName(String stationName,String laneName);
    Boolean existsByStationName(String stationName);
}