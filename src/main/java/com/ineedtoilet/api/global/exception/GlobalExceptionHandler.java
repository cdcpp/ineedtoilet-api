package com.ineedtoilet.api.global.exception;

import com.ineedtoilet.api.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 잘못된 파라미터가 들어왔을 때 (예: 파일이 비어있음)
     * HTTP 상태 코드는 400(Bad Request)으로 내리고, 에러 메시지를 반환합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.error(e.getMessage());
    }

    /**
     * 서버 내부에서 예상치 못한 치명적 에러가 발생했을 때 (예: DB 접속 끊김)
     * HTTP 상태 코드는 500(Internal Server Error)으로 내립니다.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.error("서버 내부 오류가 발생했습니다. Code Brown 긴급 출동 요망.");
    }
}
