package org.smwu.andogam.Review.controller;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Review.dto.ReviewResponseDto;
import org.smwu.andogam.Review.dto.ReviewSaveDto;
import org.smwu.andogam.Review.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/stationInfo/{stationCode}/reviewSave")
    public ReviewSaveDto commentSave(@PathVariable String stationCode, @RequestBody ReviewSaveDto reviewSaveDto) {
        return reviewService.reviewSave(stationCode,reviewSaveDto);
    }

    @GetMapping("/stationInfo/{stationCode}/reviews")
    public List<ReviewResponseDto> categoryList(@PathVariable String stationCode){
        return reviewService.reviewResponse(stationCode);
    }


}
