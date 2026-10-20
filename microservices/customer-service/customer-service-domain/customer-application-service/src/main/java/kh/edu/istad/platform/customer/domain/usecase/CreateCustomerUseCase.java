package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.DuplicateResourceException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<Customer> create(Customer customer) {
        return create(customer, customer.getSegment() != null ? customer.getSegment().getId() : null);
    }

    public Mono<Customer> create(Customer customer, String segmentId) {
        log.info("Creating customer with username: {}", customer.getUsername());

        return customerRepository.existsByUsername(customer.getUsername())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DuplicateResourceException(
                                "Customer with username '" + customer.getUsername() + "' already exists"));
                    }
                    if (customer.getEmail() != null && StringUtils.hasText(customer.getEmail().value())) {
                        return customerRepository.existsByEmail(customer.getEmail().value());
                    }
                    return Mono.just(false);
                })
                .flatMap(emailExists -> {
                    if (Boolean.TRUE.equals(emailExists)) {
                        return Mono.error(new DuplicateResourceException(
                                "Customer with email '" + customer.getEmail().value() + "' already exists"));
                    }
                    if (StringUtils.hasText(segmentId)) {
                        return customerSegmentRepository.findById(segmentId.trim())
                                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                                        "Customer segment with id '" + segmentId + "' not found")))
                                .doOnNext(customer::assignSegment)
                                .then(Mono.just(customer));
                    }
                    return Mono.just(customer);
                })
                .flatMap(validCustomer -> {
                    validCustomer.initiateCustomer();
                    return customerRepository.save(validCustomer);
                });
    }
}
