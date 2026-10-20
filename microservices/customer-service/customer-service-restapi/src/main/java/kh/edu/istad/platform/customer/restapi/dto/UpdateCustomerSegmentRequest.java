package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import lombok.Builder;

@Builder
public record UpdateCustomerSegmentRequest(
        @NotBlank(message = "Segment name is required")
        String name,

        @NotNull(message = "Segment type is required")
        CustomerSegmentType type,

        String description
) {
}
