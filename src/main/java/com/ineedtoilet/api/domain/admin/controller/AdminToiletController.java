package com.ineedtoilet.api.domain.admin.controller;

import com.ineedtoilet.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/toilets")
@RequiredArgsConstructor
public class AdminToiletController {
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.ACCEPTED) // 202 응답 고정
    public ApiResponse<Void> uploadToiletExcelData(@RequestParam("file") MultipartFile file) {

        // 1. 파일 검증 (실무에서는 이 부분도 별도 Validator나 커스텀 예외로 뺌)
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어 있습니다.");
            // GlobalExceptionHandler에서 잡아 400 Bad Request 포맷으로 변환됨
        }

        // 2. 비동기 백그라운드 작업 지시 (Service 내부에서 @Async 동작)
        adminToiletUploadService.processExcelUploadAsync(file);

        // 3. 통일된 공통 포맷으로 응답
        return ApiResponse.success("데이터 업로드 및 좌표 변환 작업이 대기열에 등록되었습니다.");
    }
}
