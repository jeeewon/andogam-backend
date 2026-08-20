package org.smwu.andogam.Common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 프로젝트 전체에서 발생하는 예외를 일관된 JSON 형식으로 응답한다.
 * 각 Controller/Service에서 try-catch로 흩어져 처리하던 로직을 여기 한 곳으로 모은다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 존재하지 않는 stationCode 등 사용자 입력 문제 -> 400
    @ExceptionHandler(InvalidStationException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidStation(InvalidStationException e) {
        log.warn("잘못된 역 코드 요청: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(e.getMessage())
                        .build());
    }

    // Route 탐색 실패(경로 없음, 탐색범위 초과 등) -> 404
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalState(IllegalStateException e) {
        log.warn("경로 탐색 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(e.getMessage())
                        .build());
    }

    // ODsay 등 외부 API 문제 -> 502
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponseDto> handleExternalApi(ExternalApiException e) {
        log.error("외부 API 호출 실패: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponseDto.builder()
                        .status(HttpStatus.BAD_GATEWAY.value())
                        .message("외부 대중교통 정보 API 호출에 실패했습니다. 잠시 후 다시 시도해주세요.")
                        .build());
    }

    // 아직 ExternalApiException으로 감싸지 못한 IOException들에 대한 최후 방어선 -> 502
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDto> handleIOException(IOException e) {
        log.error("외부 API 통신 오류: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponseDto.builder()
                        .status(HttpStatus.BAD_GATEWAY.value())
                        .message("외부 대중교통 정보 API 호출에 실패했습니다. 잠시 후 다시 시도해주세요.")
                        .build());
    }

    // 그 외 예상 못한 모든 예외 -> 500 (스택트레이스를 그대로 노출하지 않는다)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception e) {
        log.error("예상하지 못한 서버 오류: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDto.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("서버에서 알 수 없는 오류가 발생했습니다.")
                        .build());
    }
}
