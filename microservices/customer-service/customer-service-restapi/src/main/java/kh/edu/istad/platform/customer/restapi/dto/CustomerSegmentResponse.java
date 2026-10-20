package kh.edu.istad.platform.customer.restapi.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import lombok.Builder;

@Builder
public record CustomerSegmentResponse(
        String id,
        String name,
        CustomerSegmentType type,
        String description
) {
}
