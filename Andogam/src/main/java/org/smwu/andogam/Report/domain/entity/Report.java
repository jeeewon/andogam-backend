package org.smwu.andogam.Report.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportIdx;
    @CreatedDate
    private String createdDate;
    private Integer discomfort;
    // 로컬 Station 테이블(마스터 데이터, 현재 비어있음)에 의존하지 않고
    // ODsay stationID를 문자열로 직접 저장한다.
    private String stationCode;
}
