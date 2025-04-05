package glym.glym_spring.global.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {
    private final String message;
    private int status;
    private T data;

    //성공시
    public ApiResponse(T data, String message) {
        this.data = data;
        this.status = HttpStatus.OK.value();
        this.message = message;
    }

    //실패시
    public ApiResponse(T data, int status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T user, String message) {
        return new ApiResponse<>(user, message);
    }

    public static <T> ApiResponse<T> error(T data, int status, String message) {
        return new ApiResponse<>(data, status, message);
    }
    }
