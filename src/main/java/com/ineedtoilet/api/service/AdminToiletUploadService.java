package com.ineedtoilet.api.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.ineedtoilet.api.dto.ToiletExcelDto;
import com.ineedtoilet.api.entity.Toilet;
import com.ineedtoilet.api.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminToiletUploadService {

    private final ToiletRepository toiletRepository;
    private static final int BATCH_SIZE = 1000;

    @Async("excelParsingExecutor")
    public void processExcelUploadAsync(File tempFile, String traceId) {
        MDC.put("traceId", traceId);
        log.info("엑셀 파싱 시작. 파일크기: {} bytes", tempFile.length());

        try {
           final List<ToiletExcelDto> parsedDataList = new ArrayList<>();

            EasyExcel.read(tempFile, ToiletExcelDto.class, new ReadListener<ToiletExcelDto>() {
                @Override
                public void invoke(ToiletExcelDto data, AnalysisContext context) {
                    parsedDataList.add(data);
                    if (parsedDataList.size() >= BATCH_SIZE) {
                        processBatch(parsedDataList);
                        parsedDataList.clear();
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    if (!parsedDataList.isEmpty()) {
                        processBatch(parsedDataList);
                    }
                    log.info("엑셀 데이터 전체 파싱 및 DB 적재 완료.");
                }
            }).sheet().doRead();

        } catch (Exception e) {
            log.error("엑셀 처리 중 오류 발생", e);
        } finally {
            if (tempFile.exists()) tempFile.delete();
            MDC.clear();
        }
    }

    // 내부 호출이므로 @Transactional을 제거했습니다.
    protected void processBatch(List<ToiletExcelDto> batchList) {
        List<Toilet> entitiesToSave = new ArrayList<>();

        for (ToiletExcelDto dto : batchList) {
            //화장실명이 없는 경우
            if (!StringUtils.hasText(dto.getToiletName())) {
                log.warn("화장실명이 누락된 데이터 발견, 저장을 건너뜁니다. 주소: {}", dto.getRoadAddress());
                continue;
            }

            Toilet toilet = Toilet.builder()
                    .toiletName(dto.getToiletName())
                    .category(dto.getCategory())
                    .ownership(dto.getOwnership())
                    .roadAddress(dto.getRoadAddress())
                    .parcelAddress(dto.getParcelAddress())
                    .openTime(dto.getOpenTime())
                    .openTimeDetail(dto.getOpenTimeDetail())
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .build();

            entitiesToSave.add(toilet);
        }

        // 유효한 데이터만 필터링하여 일괄 저장 (saveAll 내부 트랜잭션 작동)
        if (!entitiesToSave.isEmpty()) {
            toiletRepository.saveAll(entitiesToSave);
            log.debug("배치 처리 완료: {}건 저장 성공", entitiesToSave.size());
        }
    }
}