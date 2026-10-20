package kh.edu.istad.platform.customer.restapi.dto;

import lombok.Builder;

@Builder
public record CustomerFilterRequest(
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber,
        String status,
        String segmentId,
        String segmentName,
        String keyword,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
}
