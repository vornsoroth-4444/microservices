package kh.edu.istad.platform.order.domain.service;

import kh.edu.istad.platform.order.domain.dto.CustomerResponse;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerServiceClient customerServiceClient;

    public CustomerResponse getCustomer(String customerId) {
        return customerServiceClient.getCustomer(customerId);
    }
}
