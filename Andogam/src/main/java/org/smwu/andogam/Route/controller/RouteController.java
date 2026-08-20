package org.smwu.andogam.Route.controller;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Route.dto.RouteRequestDto;
import org.smwu.andogam.Route.dto.RouteResponseDto;
import org.smwu.andogam.Route.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    // 출발역/도착역 stationCode(ODsay stationID)를 받아 일반 경로 + 접근성 우선 경로를 함께 반환
    @PostMapping(value = "/route", produces = "application/json; charset=utf8")
    public RouteResponseDto findRoute(@RequestBody RouteRequestDto requestDto) throws IOException {
        return routeService.findRoute(requestDto);
    }
}
