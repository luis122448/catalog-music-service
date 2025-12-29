package pe.bbg.music.catalog.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.bbg.music.catalog.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        return ResponseEntity.internalServerError().body(
                ApiResponse.error(
                        "An unexpected error occurred.",
                        ex.getMessage(),
                        "SYSTEM"
                )
        );
    }
}
