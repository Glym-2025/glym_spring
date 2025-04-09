package glym.glym_spring.domain.aiserverclient.domain;

import glym.glym_spring.domain.aiserverclient.dto.AIProcessingRequest;
import glym.glym_spring.domain.aiserverclient.dto.AIProcessingResponse;
import glym.glym_spring.global.exception.domain.AIServerConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIServerClient {

    private final RestClient restClient;

    @Value("${ai-server.base-url}")
    private String aiServerBaseUrl;

    @Value("${ai-server.process-endpoint}")
    private String processEndpoint;

    @Value("${backend-server.callback-url}")
    private String callbackUrl;

    /**
     * AI 서버로 이미지 처리 요청을 전송합니다.
     * AI 서버는 처리 완료 후 callbackUrl로 결과를 POST합니다.
     *
     * @param s3ImageKey S3에 저장된 이미지 키
     * @param fontName   생성할 폰트 이름
     * @param userId     사용자 ID
     * @param jobId    작업 식별자, creation ID 사용
     * @return 요청이 정상적으로 AI 서버에 전송되었는지 여부
     */
    public boolean sendToAIServer(String s3ImageKey, String fontName, Long userId,Long jobId) {
        try {
            // AI 서버의 처리 엔드포인트 URI 생성
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(aiServerBaseUrl)
                    .path(processEndpoint)
                    .build()
                    .toUri();

            // AI 서버에 전달할 요청 데이터
            // jobId를 포함해 AI 서버가 이를 콜백에 재사용하도록
            AIProcessingRequest request = AIProcessingRequest.builder()
                    .userId(userId)
                    .s3ImageKey(s3ImageKey)
                    .callbackUrl(callbackUrl)
                    .fontName(fontName)
                    .jobId(jobId) // 통합 ID로 사용
                    .build();

            log.info("Sending request to AI server. jobId: {}, userId: {}, fontName: {}, callbackUrl: {}",
                    jobId, userId, fontName, callbackUrl);

            // RestClient를 사용한 POST 요청
            AIProcessingResponse response = restClient.post()
                    .uri(uri)
                    .body(request)
                    .retrieve()
                    .onStatus(status -> status != HttpStatus.OK, (req, res) -> {
                        log.error("AI server returned error. jobId: {}, status: {}", jobId, res.getStatusCode());
                        throw new AIServerConnectionException("AI 서버 응답 오류: " + res.getStatusCode());
                    })
                    .toEntity(AIProcessingResponse.class)
                    .getBody();

            // 응답이 null이 아니면 요청 성공, DB에 상태 저장
            if (response != null) {
                log.info("AI server accepted the request. jobId: {}", jobId);
                // DB에 초기 상태 저장
                return true;
            } else {
                log.error("AI server returned null response. jobId: {}", jobId);
                return false;
            }

        } catch (Exception e) {
            log.error("Error connecting to AI server. jobId: {}, message: {}", jobId, e.getMessage());
            throw new AIServerConnectionException("AI 서버 연결 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}