package com.ineedtoilet.api.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success; // 성공 여부
    private final String message;  // 프론트엔드에 띄워줄 알림 메시지
    private final T data;          // 실제 넘겨줄 데이터 (제네릭)

    // 성공 - 데이터만 넘길 때
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    // 성공 - 메시지와 데이터를 함께 넘길 때
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 성공 - 데이터 없이 메시지만 넘길 때 (예: 파일 업로드 접수 완료)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // 에러 발생 시
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
