package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.CustomerId;


import java.util.UUID;

public record GetCustomerResult(
        String customerId,
        String username,
        String familyName,
        String givenName,
        String status
) {
}
