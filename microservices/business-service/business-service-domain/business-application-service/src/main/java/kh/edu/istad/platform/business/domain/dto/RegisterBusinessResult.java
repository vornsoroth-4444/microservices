package kh.edu.istad.platform.business.domain.dto;

import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterBusinessResult(
        UUID businessId,
        BusinessStatus status,
        String message
) {
}
