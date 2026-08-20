package org.smwu.andogam.Review.dto;

import lombok.Getter;
import org.smwu.andogam.Review.domain.entity.Review;
import org.smwu.andogam.Station.domain.Entity.Station;

@Getter
public class ReviewResponseDto {
    private Long reviewIdx;
    private String createdDate;
    private String content;

    public ReviewResponseDto(Review review){
        this.reviewIdx = review.getReviewIdx();
        this.createdDate = review.getCreatedDate();
        this.content = review.getContent();
    }
}
