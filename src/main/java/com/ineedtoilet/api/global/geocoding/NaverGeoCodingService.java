package com.ineedtoilet.api.global.geocoding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;

@Service
public class NaverGeoCodingService implements GeoCodingClient {

    @Value("${naver.map.client.id}")
    private String clientId;

    @Value("${naver.map.client.secret}")
    private String clientSecret;

    // 네이버 Geocoding API 엔드포인트
    private static final String NAVER_GEOCODE_URL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public double[] getCoordinates(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.set("X-NCP-APIGW-API-KEY", clientSecret);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI uri = UriComponentsBuilder.fromHttpUrl(NAVER_GEOCODE_URL)
                    .queryParam("query", address)
                    .build()
                    .encode()
                    .toUri();

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode addresses = rootNode.path("addresses");

            if (addresses.isArray() && addresses.size() > 0) {
                // 가장 정확도가 높은 첫 번째 결과 추출
                JsonNode firstAddress = addresses.get(0);
                double x = firstAddress.path("x").asDouble(); // 경도
                double y = firstAddress.path("y").asDouble(); // 위도
                return new double[]{x, y};
            }

        } catch (Exception e) {
            // 네트워크 오류나 파싱 실패 시 로그 기록
            System.err.println("네이버 좌표 변환 실패 - 주소: " + address + " / 원인: " + e.getMessage());
        }

        return null;
    }
}
