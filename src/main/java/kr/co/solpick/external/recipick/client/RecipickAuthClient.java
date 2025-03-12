package kr.co.solpick.external.recipick.client;

import kr.co.solpick.external.recipick.dto.RecipickAuthRequestDTO;
import kr.co.solpick.external.recipick.dto.RecipickMemberRequestDTO;
import kr.co.solpick.external.recipick.dto.RecipickMemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecipickAuthClient {

    private final RestTemplate restTemplate;

    @Value("${recipick.api.base-url}")
    private String baseUrl;

    @Value("${recipick.api.key}")
    private String apiKey;

    /**
     * 레시픽 API를 통해 사용자 인증
     */
    public boolean authenticate(String email, String password) {
        String url = baseUrl + "/api/auth/authenticate";

        RecipickAuthRequestDTO request = new RecipickAuthRequestDTO();
        request.setEmail(email);
        request.setPassword(password);
        request.setApiKey(apiKey);

        try {
            ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
            return response.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("레시픽 인증 실패: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("레시픽 인증 시 오류 발생", e);
            return false;
        }
    }

    /**
     * 레시픽 API를 통해 사용자 정보 조회
     */
    public RecipickMemberResponseDTO getUserInfo(String email) {
        String url = baseUrl + "/api/members/info";

        RecipickMemberRequestDTO request = new RecipickMemberRequestDTO();
        request.setEmail(email);
        request.setApiKey(apiKey);

        try {
            ResponseEntity<RecipickMemberResponseDTO> response =
                    restTemplate.postForEntity(url, request, RecipickMemberResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("레시픽 사용자 정보 조회 실패: 상태 코드 {}", response.getStatusCode());
                throw new RuntimeException("레시픽 API에서 사용자 정보 조회 실패");
            }
        } catch (HttpClientErrorException e) {
            log.error("레시픽 사용자 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("레시픽 API에서 사용자 정보 조회 실패", e);
        } catch (Exception e) {
            log.error("레시픽 사용자 정보 조회 시 오류 발생", e);
            throw new RuntimeException("레시픽 API에서 사용자 정보 조회 실패", e);
        }
    }
}