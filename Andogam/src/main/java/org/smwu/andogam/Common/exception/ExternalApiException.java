package org.smwu.andogam.Common.exception;

/**
 * ODsay 등 외부 API 호출/응답 처리에 실패했을 때 던지는 예외.
 * 우리 서버 잘못이 아니라 외부 요인이므로 502로 응답한다.
 */
public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
