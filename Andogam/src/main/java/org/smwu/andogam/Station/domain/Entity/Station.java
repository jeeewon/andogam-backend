package org.smwu.andogam.Station.domain.Entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity
public class Station {
    private Long stationIdx;
    private String railOprIsttCd;
    private String laneCode;
    private String laneName;
    @Id
    private String stationCode;
    private String stationName;
}