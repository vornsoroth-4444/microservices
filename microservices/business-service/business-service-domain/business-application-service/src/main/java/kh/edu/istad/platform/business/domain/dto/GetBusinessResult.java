package kh.edu.istad.platform.business.domain.dto;

import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GetBusinessResult(
        UUID businessId,
        String name,
        BusinessAddress address,
        BusinessContact contact,
        BusinessStatus status
) {
}
