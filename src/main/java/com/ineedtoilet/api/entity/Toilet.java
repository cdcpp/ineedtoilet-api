package com.ineedtoilet.api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "toilet")
public class Toilet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String toiletName;     // 화장실명 (예: "홍대어린이공원 화장실")

    private String category;       // 구분명 (예: "공중화장실", "개방화장실")
    private String ownership;      // 화장실소유 (예: "공공기관", "법인", "개인")

    private String roadAddress;    // 소재지도로명주소 (예: "서울특별시 마포구 와우산로...")
    private String parcelAddress;  // 소재지지번주소 (예: "서울특별시 마포구 서교동...")

    private String openTime;       // 개방시간 (예: "상시", "정시")
    private String openTimeDetail; // 개방시간상세 (예: "05:00~24:00")

    private Double latitude;       // 위도 (Y좌표)
    private Double longitude;      // 경도 (X좌표)

    @Builder
    public Toilet(String toiletName, String category, String ownership,
                  String roadAddress, String parcelAddress,
                  String openTime, String openTimeDetail,
                  Double latitude, Double longitude) {
        this.toiletName = toiletName;
        this.category = category;
        this.ownership = ownership;
        this.roadAddress = roadAddress;
        this.parcelAddress = parcelAddress;
        this.openTime = openTime;
        this.openTimeDetail = openTimeDetail;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //  API로 찾은 위경도를 업데이트하는 메서드
    public void updateCoordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}