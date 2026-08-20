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
public class Lift {
    @Id
    private Long liftIdx;
    private String stationName;
    private String laneName;
    private String liftNum;
    private String liftCode;
    private String liftRoute;
    private String liftLocation;
}
