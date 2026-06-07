package com.ineedtoilet.api.controller;

import com.ineedtoilet.api.global.response.ApiResponse;
import com.ineedtoilet.api.service.AdminToiletUploadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/toilets")
@RequiredArgsConstructor
public class AdminToiletController {

    private final AdminToiletUploadService adminToiletUploadService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.ACCEPTED) // 202 Accepted: 요청은 접수되었고 처리는 백그라운드에서 됨
    public ApiResponse<Void> uploadToiletExcelData(@RequestParam("file") MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 엑셀 파일이 비어 있습니다.");
        }

        // 요청 고유 ID (TraceID) 생성 및 메인 스레드 MDC 저장
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);

        // 로컬 임시 파일로 안전하게 복사
        File tempFile = File.createTempFile("ineedtoilet-upload-", ".xlsx");
        file.transferTo(tempFile);


        adminToiletUploadService.processExcelUploadAsync(tempFile, traceId);

        // 클라이언트에게는 즉시 성공 응답 반환
        return ApiResponse.success("데이터 파싱 및 적재 작업이 백그라운드 대기열에 등록되었습니다.");
    }
}