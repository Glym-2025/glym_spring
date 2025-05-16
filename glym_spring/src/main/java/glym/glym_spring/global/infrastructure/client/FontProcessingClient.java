package glym.glym_spring.global.infrastructure.client;

import glym.glym_spring.domain.font.dto.AIRequestDto;
import glym.glym_spring.domain.font.dto.AIRequestAckDto;
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
public class FontProcessingClient {

    private final RestClient restClient;

    @Value("${ai-server.base-url}")
    private String aiServerBaseUrl;

    @Value("${ai-server.process-endpoint}")
    private String processEndpoint;

    public void sendProcessingRequest(AIRequestDto request) {
        try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(aiServerBaseUrl)
                    .path(processEndpoint)
                    .build()
                    .toUri();

            log.info("Sending request to AI server. jobId: {}, userId: {}, fontName: {}, callbackUrl: {}",
                    request.getJobId(), request.getUserId(), request.getFontName(), request.getCallbackUrl());

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

            if (response == null) {
                log.error("AI server returned null response. jobId: {}", request.getJobId());
                throw new CustomException(AI_SERVER_ERROR);
            }

            log.info("AI server accepted the request. jobId: {}", request.getJobId());

        } catch (Exception e) {
            log.error("Error connecting to AI server. jobId: {}, message: {}", request.getJobId(), e.getMessage());
            throw new CustomException(AI_CONNECTION_ERROR);
        }
    }
}