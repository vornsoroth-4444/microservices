package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Customer execute(UUID customerId) {
        log.info("Fetching customer with id: {}", customerId);
        return customerRepository.findById(new CustomerId(customerId))
                .orElseThrow(() -> new CustomerDomainException("Customer not found with id: " + customerId));
    }
}
