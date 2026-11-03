package kh.edu.istad.platform.order.exception;

import kh.edu.istad.platform.order.client.exception.DownStreamServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DownStreamServiceException.class)
    public ResponseEntity<?> handle(DownStreamServiceException exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "service" , exception.getService(),
                                "message" , "Downstream service error",
                                "details" , exception.getMessage()
                        )
                );
    }
}
