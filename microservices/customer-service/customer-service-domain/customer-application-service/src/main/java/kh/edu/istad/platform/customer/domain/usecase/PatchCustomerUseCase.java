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
public class PatchCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<Customer> patch(
            UUID customerId,
            String familyName,
            String givenName,
            String email,
            String phoneNumber,
            String segmentId
    ) {
        log.info("Patching customer id: {}", customerId);
        CustomerId id = new CustomerId(customerId);

        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer with id '" + customerId + "' not found")))
                .flatMap(customer -> {
                    // Check email uniqueness if email provided and changed
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
                    String updatedFamilyName = StringUtils.hasText(familyName) ? familyName.trim() : customer.getFamilyName();
                    String updatedGivenName = StringUtils.hasText(givenName) ? givenName.trim() : customer.getGivenName();
                    Email updatedEmail = StringUtils.hasText(email) ? new Email(email.trim()) : customer.getEmail();
                    PhoneNumber updatedPhone = StringUtils.hasText(phoneNumber) ? new PhoneNumber(phoneNumber.trim()) : customer.getPhoneNumber();

                    if (StringUtils.hasText(segmentId)) {
                        return customerSegmentRepository.findById(segmentId.trim())
                                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                                        "Customer segment with id '" + segmentId + "' not found")))
                                .map(segment -> {
                                    customer.updateDetails(updatedFamilyName, updatedGivenName, updatedEmail, updatedPhone, segment);
                                    return customer;
                                });
                    } else {
                        customer.updateDetails(updatedFamilyName, updatedGivenName, updatedEmail, updatedPhone, customer.getSegment());
                        return Mono.just(customer);
                    }
                })
                .flatMap(customerRepository::save);
    }
}
