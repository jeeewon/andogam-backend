package org.smwu.andogam.Review.dto;

import lombok.Data;
import org.smwu.andogam.Review.domain.entity.Review;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ReviewSaveDto {
    private Long reviewIdx;
    private String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    private String content;
    private String stationCode;

    public Review toEntity() {
        Review review = Review.builder()
                .reviewIdx(this.reviewIdx)
                .createdDate(this.createdDate)
                .content(this.content)
                .stationCode(this.stationCode)
                .build();
        return review;
    }
}
