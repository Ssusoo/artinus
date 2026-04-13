package com.artinus.api.exception;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalApiUnavailableException.class)
    public ProblemDetail handleExternalApiUnavailable(ExternalApiUnavailableException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        detail.setTitle("외부 API 호출 실패");
        detail.setDetail(e.getMessage());
        return detail;
    }

    @ExceptionHandler(ExternalApiRejectedException.class)
    public ProblemDetail handleExternalApiRejected(ExternalApiRejectedException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("외부 API 검증 실패");
        detail.setDetail(e.getMessage());
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("잘못된 요청입니다.");
        detail.setDetail(e.getMessage());
        return detail;
    }
}
