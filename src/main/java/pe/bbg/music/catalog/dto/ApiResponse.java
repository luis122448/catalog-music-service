package pe.bbg.music.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.bbg.music.catalog.dto.enums.ResponseStatusEnum;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private ResponseStatusEnum status;
    private String message;
    private T data;

    private String logUser;
    private String logMessage;
    private LocalDateTime logDate;

    public Optional<T> data() {
        return Optional.ofNullable(data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(ResponseStatusEnum.SUCCESS)
                .message(message)
                .data(data)
                .logUser(pe.bbg.music.catalog.security.SecurityUtils.getCurrentUser())
                .logDate(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String logMessage, String logUser) {
        return ApiResponse.<T>builder()
                .status(ResponseStatusEnum.ERROR)
                .message(message)
                .logMessage(logMessage)
                .logUser(logUser != null ? logUser : pe.bbg.music.catalog.security.SecurityUtils.getCurrentUser())
                .logDate(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> warning(String message) {
        return ApiResponse.<T>builder()
                .status(ResponseStatusEnum.WARNING)
                .message(message)
                .logUser(pe.bbg.music.catalog.security.SecurityUtils.getCurrentUser())
                .logDate(LocalDateTime.now())
                .build();
    }
}