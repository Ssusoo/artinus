package com.artinus.api.global.exception;

import com.artinus.api.global.client.exception.ClientException;
import com.artinus.api.global.client.exception.ExternalApiRejectedException;
import com.artinus.api.global.client.exception.ExternalApiUnavailableException;
import com.artinus.core.exception.CannelException;
import com.artinus.core.exception.SubscriptionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

    @ExceptionHandler(ClientException.class)
    public ProblemDetail handleClientException(ClientException e) {
        System.out.println("### handleClientException called ###");
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("구독 처리 실패");
        detail.setDetail(e.getMessage());
        return detail;
    }

    @ExceptionHandler(SubscriptionException.class)
    public ProblemDetail handleSubscriptionException(SubscriptionException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("구독 상태 변경 실패");
        detail.setDetail(e.getMessage());
        return detail;
    }

    @ExceptionHandler(CannelException.class)
    public ProblemDetail handleCannelException(CannelException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("채널 처리 실패");
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("요청값 검증 실패");
        detail.setDetail(message);
        return detail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("잘못된 요청 형식입니다.");
        detail.setDetail("요청 본문 형식이 올바르지 않거나 구독 상태 값이 잘못되었습니다.");
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        e.printStackTrace();

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("서버 내부 오류");
        detail.setDetail("요청 처리 중 오류가 발생했습니다.");
        return detail;
    }
}
