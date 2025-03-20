package kr.co.solpick.external.recipick.client;

import kr.co.solpick.external.recipick.dto.RecipickRequestDTO;
import kr.co.solpick.external.recipick.dto.RecipickLikeResponseDTO;
import kr.co.solpick.external.recipick.dto.RecipickStepRequestDTO;
import kr.co.solpick.external.recipick.dto.RecipickStepResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecipickRecipeApiClient {

    private final RestTemplate restTemplate;

    @Value("${recipick.api.base-url}")
    private String baseUrl;

    @Value("${recipick.api.key}")
    private String apiKey;

    /**
     * 레시픽 API를 통해 사용자의 좋아요 레시피 목록 조회
     *
     * @param memberId 사용자 ID
     * @return 좋아요한 레시피 목록, 오류 발생 시 빈 리스트 반환
     */
    public List<RecipickLikeResponseDTO> getLikedRecipes(int memberId) {
        try {
            log.info("레시픽 API 좋아요 레시피 목록 요청: memberId={}", memberId);

            String url = baseUrl + "/api/recipe/likes";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RecipickRequestDTO requestDTO = new RecipickRequestDTO();
            requestDTO.setMemberId(memberId);
            requestDTO.setApiKey(apiKey);

            HttpEntity<RecipickRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

            ResponseEntity<RecipickLikeResponseDTO[]> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    RecipickLikeResponseDTO[].class
            );

            // 응답 상태 코드에 따른 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                List<RecipickLikeResponseDTO> result = Arrays.asList(response.getBody());
                log.info("레시픽 API 좋아요 레시피 목록 응답: {} 개", result.size());
                return result;
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                log.info("좋아요한 레시피가 없습니다: memberId={}", memberId);
                return Collections.emptyList();
            } else {
                log.error("레시픽 API 좋아요 레시피 목록 조회 실패: 상태 코드 {}", response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("레시픽 API 좋아요 레시피 목록 조회 실패", e);
            return Collections.emptyList();
        }
    }

    /**
     * 레시픽 API를 통해 레시피의 스텝 정보 조회
     *
     * @param recipeId 레시피 ID
     * @return 레시피 스텝 목록, 오류 발생 시 빈 리스트 반환
     */
    public List<RecipickStepResponseDTO> getRecipeSteps(int recipeId) {
        try {
            log.info("레시픽 API 레시피 스텝 조회 요청: recipeId={}", recipeId);

            String url = baseUrl + "/api/recipe/steps";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RecipickStepRequestDTO requestDTO = new RecipickStepRequestDTO();
            requestDTO.setRecipeId(recipeId);
            requestDTO.setApiKey(apiKey);

            HttpEntity<RecipickStepRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

            ResponseEntity<RecipickStepResponseDTO[]> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    RecipickStepResponseDTO[].class
            );

            // 응답 상태 코드에 따른 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                List<RecipickStepResponseDTO> result = Arrays.asList(response.getBody());
                log.info("레시픽 API 레시피 스텝 조회 응답: {} 개", result.size());
                return result;
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                log.info("레시피 스텝이 없습니다: recipeId={}", recipeId);
                return Collections.emptyList();
            } else {
                log.error("레시픽 API 레시피 스텝 조회 실패: 상태 코드 {}", response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("레시픽 API 레시피 스텝 조회 실패", e);
            return Collections.emptyList();
        }
    }
}