package kh.edu.istad.platform.business.domain.dto;

import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import lombok.Builder;

@Builder
public record RegisterBusinessCommand(
        String name,
        BusinessAddress address,
        BusinessContact contact
) {
}
