package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.Builder;

@Builder
public record CustomerStatistic(
        CustomerStatus status,
        Long total
) {
}
