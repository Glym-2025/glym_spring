package glym.glym_spring.domain.font.docs;

import glym.glym_spring.domain.font.dto.FontCreateRequest;
import glym.glym_spring.domain.font.dto.FontListResponseDto;
import glym.glym_spring.domain.font.dto.JobStatusResponseDto;
import glym.glym_spring.domain.font.dto.SuccessResponse;
import glym.glym_spring.global.exception.ErrorResponse;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Tag(name = "Font", description = "폰트 관련 API")
public interface FontControllerDocs {

    @Operation(
            summary = "폰트 생성",
            description = """
            사용자가 업로드한 손글씨 이미지를 기반으로 폰트를 생성합니다.
            
            **요청 형식**
            - multipart/form-data 형식
            
            **응답**
            - 200 OK: 폰트 생성 성공
                {
                  "message": "Font created successfully"
                }
            - 400 Bad Request: 이미지 파일이 유효하지 않거나 누락된 경우
                {
                  "errorCode": "INVALID_IMAGE",
                  "errorMessage": "유효하지 않은 이미지 파일입니다"
                }
            - 500 Internal Server Error: 폰트 생성 중 서버 오류 발생
                {
                  "errorCode": "SERVER_ERROR",
                  "errorMessage": "폰트 생성 중 오류가 발생했습니다"
                }
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "폰트 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<?> createFont(
            @RequestBody(
                    description = "폰트 생성 요청",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = FontCreateRequest.class)
                    )
            )
            FontCreateRequest request
    ) throws ImageValidationException, IOException;

    @Operation(
            summary = "폰트 생성 상태 스트리밍",
            description = """
            주어진 jobId에 해당하는 폰트 생성 작업의 상태를 실시간으로 스트리밍합니다. Server-Sent Events (SSE)를 사용하여 데이터를 전송합니다.

            **요청**
            - jobId: 작업 ID (경로 파라미터)

            **응답 형식**
            - MIME 타입: `text/event-stream`
            - 각 이벤트는 SSE 형식으로 전송되며, `data:` 필드에 JSON 문자열 포함
            - 예시:
                ```
                data: {"status": "PROGRESSING"}
                data: {"status": "COMPLETED", "fontUrl": "https://my-bucket.s3.amazonaws.com/fonts/123.ttf?presigned"}
                data: {"status": "FAILED", "errorMessage": "Invalid font format"}
                ```

            **상태 값**
            - `PROGRESSING`: 작업 진행 중
            - `COMPLETED`: 작업 완료 (fontUrl 포함)
            - `FAILED`: 작업 실패 (errorMessage 포함)

            **주의**
            - 클라이언트는 SSE 연결을 유지해야 하며, 작업이 완료되거나 실패하면 스트림이 종료됩니다.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE 스트림 시작",
                    content = @Content(
                            mediaType = "text/event-stream",
                            schema = @Schema(implementation = String.class, example = "data: {\"status\": \"PROGRESSING\"}\n\n")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "작업 ID를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    SseEmitter streamJobStatus(
            @Parameter(description = "작업 ID", required = true, example = "작업 uuid")
            @PathVariable String jobId
    );

    @Operation(
            summary = "사용자 폰트 목록 조회",
            description = """
        현재 로그인한 사용자가 생성한 모든 폰트 목록을 반환합니다.
        
        **응답**
        - 200 OK: 폰트 목록 반환
            [
              {
                "id": 1,
                "fontName": "나의 첫 폰트",
                "createdAt": "2023-05-13T14:30:45",
                "fontUrl": "https://my-bucket.s3.amazonaws.com/fonts/user_1/font_1.ttf?presigned"
              },
              ...
            ]
        - 401 Unauthorized: 인증되지 않은 사용자
        - 500 Internal Server Error: 서버 오류 발생
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "폰트 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FontListResponseDto.class, type = "array")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/fonts")
    ResponseEntity<?> getUserFonts();
}