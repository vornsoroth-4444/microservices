package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record CustomerReport(
        CustomerStatus status,
        Long total,
        List<Customer> customers
) {
}
