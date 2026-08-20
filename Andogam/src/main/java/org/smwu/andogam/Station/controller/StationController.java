package org.smwu.andogam.Station.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smwu.andogam.Station.dto.StationInfoDto;
import org.smwu.andogam.Station.service.StationService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@RestController
public class StationController {

    private final StationService stationService;

    @RequestMapping(value = "/stationInfo/{stationCode}", method = RequestMethod.GET, produces = "application/json; charset=utf8")
    public StationInfoDto StationInfo(@PathVariable String stationCode) throws IOException {
        return stationService.getStationInfo(stationCode);
    }
}
