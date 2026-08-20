package org.smwu.andogam.Station.domain.Repository;

import org.smwu.andogam.Station.domain.Entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station,Integer>{
    @Query("SELECT stationName FROM Station WHERE stationCode=:stationCode")
    String getStationName(String stationCode);
    @Query("SELECT laneName FROM Station WHERE stationCode=:stationCode")
    String getLaneName(String stationCode);
    Station findByStationCode(String stationCode);
}
