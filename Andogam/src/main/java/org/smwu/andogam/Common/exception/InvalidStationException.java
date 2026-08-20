package org.smwu.andogam.Common.exception;

/**
 * 존재하지 않는 stationCode 등, 요청 자체는 유효하지만 그 내용이 잘못된 경우 던지는 예외.
 * 클라이언트 잘못이므로 400번대 응답으로 이어진다.
 */
public class InvalidStationException extends RuntimeException {
    public InvalidStationException(String message) {
        super(message);
    }
}
