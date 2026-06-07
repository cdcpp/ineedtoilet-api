package com.ineedtoilet.api.global.exception;

import com.ineedtoilet.api.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 클라이언트가 잘못된 데이터(예: 빈 파일)를 보냈을 때 (HTTP 400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("잘못된 요청 발생: {}", e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    /**
     * 서버 내부에서 예상치 못한 치명적 에러가 발생했을 때 (HTTP 500)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("서버 내부 오류 발생", e);
        return ApiResponse.error("서버 내부 오류가 발생했습니다. 관리자에게 문의하십시오.");
    }
}