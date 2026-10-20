package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.DuplicateResourceException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<Customer> update(
            UUID customerId,
            String familyName,
            String givenName,
            String email,
            String phoneNumber,
            String segmentId
    ) {
        log.info("Updating customer id: {}", customerId);
        CustomerId id = new CustomerId(customerId);

        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer with id '" + customerId + "' not found")))
                .flatMap(customer -> {
                    // Check email uniqueness if email changed
                    if (StringUtils.hasText(email) &&
                            (customer.getEmail() == null || !email.equalsIgnoreCase(customer.getEmail().value()))) {
                        return customerRepository.existsByEmail(email)
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(new DuplicateResourceException(
                                                "Email '" + email + "' is already in use"));
                                    }
                                    return Mono.just(customer);
                                });
                    }
                    return Mono.just(customer);
                })
                .flatMap(customer -> {
                    if (StringUtils.hasText(segmentId)) {
                        return customerSegmentRepository.findById(segmentId.trim())
                                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                                        "Customer segment with id '" + segmentId + "' not found")))
                                .map(segment -> {
                                    Email emailObj = StringUtils.hasText(email) ? new Email(email) : customer.getEmail();
                                    PhoneNumber phoneObj = StringUtils.hasText(phoneNumber) ? new PhoneNumber(phoneNumber) : customer.getPhoneNumber();
                                    customer.updateDetails(familyName, givenName, emailObj, phoneObj, segment);
                                    return customer;
                                });
                    } else {
                        Email emailObj = StringUtils.hasText(email) ? new Email(email) : customer.getEmail();
                        PhoneNumber phoneObj = StringUtils.hasText(phoneNumber) ? new PhoneNumber(phoneNumber) : customer.getPhoneNumber();
                        customer.updateDetails(familyName, givenName, emailObj, phoneObj, customer.getSegment());
                        return Mono.just(customer);
                    }
                })
                .flatMap(customerRepository::save);
    }
}
