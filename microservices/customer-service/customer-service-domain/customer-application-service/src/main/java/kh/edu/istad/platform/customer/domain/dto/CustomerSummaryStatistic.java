package kh.edu.istad.platform.customer.domain.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CustomerSummaryStatistic(
        Long totalCustomers,
        Long totalActive,
        Long totalInactive,
        List<CustomerStatistic> details
) {
}
