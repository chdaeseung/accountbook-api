package chdaeseung.accountbook.global.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponseDto {
    private int status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<FieldErrorDto> fieldErrors;

    @Getter
    @Builder
    public static class FieldErrorDto {
        private String field;
        private String reason;
    }
}