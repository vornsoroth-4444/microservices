package kh.edu.istad.platform.customer.restapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        List<FieldErrorDetail> errors
) {
    public record FieldErrorDetail(String field, String message) {
    }
}
