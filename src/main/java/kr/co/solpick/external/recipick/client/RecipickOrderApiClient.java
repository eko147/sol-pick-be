package kr.co.solpick.external.recipick.client;

import kr.co.solpick.external.recipick.dto.RecipickRequestDTO;
import kr.co.solpick.order.dto.OrderHistoryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecipickOrderApiClient {

    private final RestTemplate restTemplate;

    @Value("${recipick.api.base-url}")
    private String baseUrl;

    @Value("${recipick.api.key}")
    private String apiKey;

    public List<OrderHistoryResponseDTO> getOrderHistory(int memberId) {
        try {
            log.info("레시픽 API 주문 내역 요청: memberId={}", memberId);

            String url = baseUrl + "/api/order/history";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RecipickRequestDTO requestDTO = new RecipickRequestDTO();
            requestDTO.setMemberId(memberId);
            requestDTO.setApiKey(apiKey);

            HttpEntity<RecipickRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

            OrderHistoryResponseDTO[] response = restTemplate.postForObject(
                    url,
                    requestEntity,
                    OrderHistoryResponseDTO[].class);

            List<OrderHistoryResponseDTO> result = response != null ? Arrays.asList(response) : Collections.emptyList();
            log.info("레시픽 API 주문 내역 응답: {} ", result.size());

            return result;
        } catch (Exception e) {
            log.error("레시픽 API 주문 내역 조회 실패", e);
            return Collections.emptyList();
        }
    }
}