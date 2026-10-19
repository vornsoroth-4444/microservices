package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record CustomerFilterQuery(
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber,
        CustomerStatus status,
        String keyword,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
    public static CustomerFilterQuery empty() {
        return new CustomerFilterQuery(null, null, null, null, null, null, null, null, null, null, null);
    }
}
