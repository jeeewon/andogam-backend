package org.smwu.andogam.Common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {
    private int status;
    private String message;
}
