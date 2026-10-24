package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.platform.order.domain.dto.client.CustomerClientModel;

import java.util.Optional;
import java.util.UUID;

public interface CustomerClientPort {
    Optional<CustomerClientModel> getCustomer(UUID customerId);
}
