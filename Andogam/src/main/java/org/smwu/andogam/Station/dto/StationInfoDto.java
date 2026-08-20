package org.smwu.andogam.Station.dto;

import lombok.Getter;
import lombok.Setter;
import org.smwu.andogam.Station.domain.Entity.Station;

import javax.persistence.Id;

@Getter
@Setter
public class StationInfoDto {
    private Long stationIdx;
    private String laneName;
    private String stationCode;
    private String stationName;
    private String address;
    private String tel;
    private boolean elevator;
    private boolean lift;
    private boolean ramp;

    public StationInfoDto(Station station, String address, String tel, boolean elevator, boolean lift, boolean ramp){
        this.stationIdx = station.getStationIdx();
        this.stationCode = station.getStationCode();
        this.stationName = station.getStationName();
        this.laneName = station.getLaneName();
        this.address = address;
        this.tel = tel;
        this.elevator = elevator;
        this.lift = lift;
        this.ramp = ramp;
    }

    /**
     * 로컬 Station 테이블(마스터 데이터) 없이, ODsay 응답에서 바로 받은
     * stationCode/stationName/laneName으로 생성하는 생성자.
     * 로컬 Station 테이블이 채워져 있지 않은 지금 상황에서 사용한다.
     */
    public StationInfoDto(String stationCode, String stationName, String laneName,
                           String address, String tel, boolean elevator, boolean lift, boolean ramp){
        this.stationIdx = null;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.laneName = laneName;
        this.address = address;
        this.tel = tel;
        this.elevator = elevator;
        this.lift = lift;
        this.ramp = ramp;
    }
}
