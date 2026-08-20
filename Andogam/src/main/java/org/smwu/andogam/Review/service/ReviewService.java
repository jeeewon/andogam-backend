package org.smwu.andogam.Review.service;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Review.domain.entity.Review;
import org.smwu.andogam.Review.domain.repository.ReviewRepository;
import org.smwu.andogam.Review.dto.ReviewResponseDto;
import org.smwu.andogam.Review.dto.ReviewSaveDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    //댓글 등록
    public ReviewSaveDto reviewSave(String stationCode, ReviewSaveDto reviewSaveDto){
        reviewSaveDto.setStationCode(stationCode);
        Review review = reviewSaveDto.toEntity();
        reviewRepository.save(review);

        return reviewSaveDto;
    }

    //댓글 불러오기
    public List<ReviewResponseDto> reviewResponse(String stationCode) {
        return reviewRepository.findByStation(stationCode).stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
}
