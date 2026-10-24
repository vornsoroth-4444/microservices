package kh.edu.istad.platform.customer.restapi.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private UUID customerId;
    private String username;
    private String givenName;
    private String familyName;
    private CustomerStatus status;
}
