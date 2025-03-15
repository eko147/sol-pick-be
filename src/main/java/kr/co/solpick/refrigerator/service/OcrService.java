package kr.co.solpick.refrigerator.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

@Service
@Slf4j
public class OcrService {
    private ImageAnnotatorClient visionClient;

    @Value("${google.cloud.credentials.path}")
    private Resource credentialsResource;

    @PostConstruct
    public void init() {
        try {
            // 리소스에서 InputStream 가져오기
            InputStream credentialsStream = credentialsResource.getInputStream();

            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            // Vision API 클라이언트 초기화
            visionClient = ImageAnnotatorClient.create(
                    ImageAnnotatorSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                            .build()
            );

            log.info("🟢 Google Vision API 클라이언트 초기화 완료");
        } catch (IOException e) {
            log.error("🔴 Google Vision API 클라이언트 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Google Vision API 초기화 실패", e);
        }
    }

    public String processImage(byte[] imageBytes) {
        try {
            // 이미지 준비
            ByteString imgBytes = ByteString.copyFrom(imageBytes);
            Image image = Image.newBuilder().setContent(imgBytes).build();

            // OCR 기능 설정 (한국어 텍스트 감지)
            Feature feature = Feature.newBuilder()
                    .setType(Feature.Type.TEXT_DETECTION)
                    .build();

            // 요청 생성
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(image)
                    .setImageContext(ImageContext.newBuilder()
                            .addLanguageHints("ko") // 한국어 힌트 추가
                            .build())
                    .build();

            // API 호출
            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(
                    List.of(request));

            // 결과 추출
            AnnotateImageResponse res = response.getResponses(0);

            if (res.hasError()) {
                log.error("🔴 OCR 처리 에러: {}", res.getError().getMessage());
                return "OCR 처리 중 오류가 발생했습니다: " + res.getError().getMessage();
            }

            // 감지된 텍스트 추출
            if (!res.getTextAnnotationsList().isEmpty()) {
                return res.getTextAnnotationsList().get(0).getDescription();
            }

            return "텍스트를 감지할 수 없습니다.";
        } catch (Exception e) {
            log.error("🔴 OCR 처리 중 예외 발생: {}", e.getMessage(), e);
            return "OCR 처리 실패: " + e.getMessage();
        }
    }

//    @PostConstruct
//    public void init() {
//        log.info("🟢 OCR 서비스 초기화 - 테스트 모드");
//    }
//
//    public String processImage(byte[] imageBytes) {
//        // 테스트용 반환 데이터
//        return "크런키초콜릿 1 1,000\n비타500 180ML 1 1,200\n포테이토크리스 1 1,000\n합계수량/금액 3 3,200\n과세 매출 2,909\n부가세 291\n합 계 3,200\n기프티 쿠 폰 3,200\n거래 일자 2025-03-12\n대상 금액 3,200원";
//    }
}