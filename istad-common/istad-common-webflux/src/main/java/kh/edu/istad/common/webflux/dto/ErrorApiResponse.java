package kh.edu.istad.common.webflux.dto;

import java.util.Map;

public record ErrorApiResponse(
        int status,
        String message,
        Map<String, Object> details
) {
}
