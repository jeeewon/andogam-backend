package org.smwu.andogam.Search.controller;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Search.dto.SearchDetailDto;
import org.smwu.andogam.Search.dto.SearchDto;
import org.smwu.andogam.Search.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    //대중교통 정류장 검색 (이름검색)
    @RequestMapping(value="/searchStation", method = RequestMethod.POST, produces = "application/json; charset=utf8")
    public List<SearchDetailDto> searchStation(@RequestBody SearchDto searchDto) throws  IOException{
        return searchService.searchStation(searchDto);
    }
}
