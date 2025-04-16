package glym.glym_spring.domain.font.docs;

import glym.glym_spring.domain.font.dto.FontCreateRequest;
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
import org.springframework.web.multipart.MultipartFile;

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
}