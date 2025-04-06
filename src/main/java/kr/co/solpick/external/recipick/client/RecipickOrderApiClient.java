package kr.co.solpick.external.recipick.client;

import kr.co.solpick.external.recipick.dto.RecipickRequestDTO;
import kr.co.solpick.order.dto.OrderHistoryResponseDTO;
import kr.co.solpick.refrigerator.dto.RecipickOrderItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    /* 레시픽 API를 통해 구매 재료 목록 조회 */
    public List<RecipickOrderItemDTO> getIngredients(int memberId) {
        try {
            log.info("레시픽 API 구매 재료 목록 요청: memberId={}", memberId);

            String url = baseUrl + "/api/ingredient/list";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 데이터 구성
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("member_id", memberId);
            requestMap.put("apiKey", apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);

            // API 호출 - POST 방식으로 요청
            ResponseEntity<List<RecipickOrderItemDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<RecipickOrderItemDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                List<RecipickOrderItemDTO> result = response.getBody();
                log.info("레시픽 API 구매 재료 목록 응답: {} 건", result != null ? result.size() : 0);
                return result != null ? result : Collections.emptyList();
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                log.info("레시픽 API 구매 재료 목록 없음: memberId={}", memberId);
                return Collections.emptyList();
            } else {
                log.warn("레시픽 API 구매 재료 목록 응답 실패: {}", response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("레시픽 API 구매 재료 목록 조회 실패", e);
            return Collections.emptyList();
        }
    }

}
