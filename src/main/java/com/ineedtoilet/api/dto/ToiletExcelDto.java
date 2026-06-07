package com.ineedtoilet.api.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToiletExcelDto {

    @ExcelProperty("구분명")
    private String category;

    @ExcelProperty("화장실명")
    private String toiletName;

    @ExcelProperty("소재지도로명주소")
    private String roadAddress;

    @ExcelProperty("소재지지번주소")
    private String parcelAddress;

    @ExcelProperty("개방시간")
    private String openTime;

    @ExcelProperty("개방시간상세")
    private String openTimeDetail;

    @ExcelProperty("화장실소유")
    private String ownership;

    @ExcelProperty("위도")
    private Double latitude;

    @ExcelProperty("경도")
    private Double longitude;
}