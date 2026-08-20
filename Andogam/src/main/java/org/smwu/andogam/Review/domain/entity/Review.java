package org.smwu.andogam.Review.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewIdx;
    @CreatedDate
    private String createdDate;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    // 로컬 Station 테이블(마스터 데이터, 현재 비어있음)에 의존하지 않고
    // ODsay stationID를 문자열로 직접 저장한다.
    private String stationCode;
}
