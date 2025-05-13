package glym.glym_spring.domain.aiserverclient.domain;

import glym.glym_spring.domain.aiserverclient.dto.AIRequestAckDto;
import glym.glym_spring.domain.aiserverclient.dto.AIRequestDto;
import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.global.exception.domain.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static glym.glym_spring.global.exception.errorcode.ErrorCode.*;

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


    public boolean sendToAIServer(FontProcessingJob job) {
        try {
            // AI 서버의 처리 엔드포인트 URI 생성
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(aiServerBaseUrl)
                    .path(processEndpoint)
                    .build()
                    .toUri();

            // jobId를 포함해 AI 서버가 이를 콜백에 재사용하도록
            AIRequestDto request = AIRequestDto.builder()
                    .userId(job.getUser().getId())
                    .s3ImageKey(job.getS3ImageKey())
                    .callbackUrl(callbackUrl)
                    .fontName(job.getFontName())
                    .jobId(job.getJobId()) // 통합 ID로 사용
                    .build();

            log.info("Sending request to AI server. jobId: {}, userId: {}, fontName: {}, callbackUrl: {}",
                    request.getJobId(), request.getUserId(), request.getFontName(), callbackUrl);


            // RestClient를 사용한 POST 요청
            AIRequestAckDto response = restClient.post()
                    .uri(uri)
                    .body(request)
                    .retrieve()
                    .onStatus(status -> status != HttpStatus.OK, (req, res) -> {
                        log.error("AI server returned error. jobId: {}, status: {}", request.getJobId(), res.getStatusCode());
                        throw new CustomException(AI_SERVER_ERROR);
                    })
                    .toEntity(AIRequestAckDto.class)
                    .getBody();

            // 응답이 null이 아니면 요청 성공, DB에 상태 저장
            if (response != null) {
                log.info("AI server accepted the request. jobId: {}", request.getJobId());
                return true;
            } else {
                log.error("AI server returned null response. jobId: {}", request.getJobId());
                return false;
            }

        } catch (Exception e) {
            log.error("Error connecting to AI server. jobId: {}, message: {}", job.getJobId(), e.getMessage());
            throw new CustomException(AI_CONNECTION_ERROR);
        }
    }
}