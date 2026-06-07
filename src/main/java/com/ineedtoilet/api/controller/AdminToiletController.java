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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/toilets")
@RequiredArgsConstructor
public class AdminToiletController {

    private final AdminToiletUploadService adminToiletUploadService;

    // 허용하는 엑셀 파일의 확장자 및 Content-Type 목록
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("xlsx", "xls");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.ACCEPTED) // 202 Accepted: 요청은 접수되었고 처리는 백그라운드에서 됨
    public ApiResponse<Void> uploadToiletExcelData(@RequestParam("file") MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 엑셀 파일이 비어 있습니다.");
        }

        // 엑셀 파일 유효성 검증
        validateExcelFile(file);

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

    private void validateExcelFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("파일명 또는 확장자가 존재하지 않습니다.");
        }

        // 1. 확장자 검증
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        boolean isValidExtension = ALLOWED_EXTENSIONS.stream()
                .anyMatch(ext -> ext.equals(extension));
        
        if (!isValidExtension) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. .xlsx 또는 .xls 파일만 업로드 가능합니다.");
        }

        // 2. Content-Type 검증
        String contentType = file.getContentType();
        boolean isValidContentType = contentType != null && ALLOWED_CONTENT_TYPES.stream()
                .anyMatch(type -> type.equalsIgnoreCase(contentType));

        if (!isValidContentType) {
            throw new IllegalArgumentException("잘못된 파일 타입입니다. 엑셀 파일만 업로드 가능합니다.");
        }
    }
}