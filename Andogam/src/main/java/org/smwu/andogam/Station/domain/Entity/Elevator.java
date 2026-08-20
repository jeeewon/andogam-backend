package org.smwu.andogam.Station.domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Elevator {
    @Id
    private Long elevatorIdx;
    private String stationName;
    private String laneName;
    private String elevatorNum;
    private String elevatorCode;
    private String elevatorRoute;
    private String elevatorLocation;
}
