package org.smwu.andogam.Station.domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Ramp {
    @Id
    private Long rampIdx;
    private String stationName;
    private String laneName;
    private String rampNum;
    private String rampLocation;
}
