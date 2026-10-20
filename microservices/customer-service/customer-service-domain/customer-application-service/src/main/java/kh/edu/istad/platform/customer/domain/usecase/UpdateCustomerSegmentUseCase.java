package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.DuplicateResourceException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCustomerSegmentUseCase {

    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<CustomerSegment> update(String id, String name, CustomerSegmentType type, String description) {
        log.info("Updating customer segment id: {}", id);

        return customerSegmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                        "Customer segment with id '" + id + "' not found")))
                .flatMap(segment -> {
                    if (StringUtils.hasText(name) && !name.trim().equalsIgnoreCase(segment.getName())) {
                        return customerSegmentRepository.findByName(name.trim())
                                .flatMap(existingWithSameName -> {
                                    if (!existingWithSameName.getId().equals(id)) {
                                        return Mono.error(new DuplicateResourceException(
                                                "Customer segment with name '" + name + "' already exists"));
                                    }
                                    return Mono.just(segment);
                                })
                                .switchIfEmpty(Mono.just(segment));
                    }
                    return Mono.just(segment);
                })
                .flatMap(segment -> {
                    segment.update(name, type, description);
                    return customerSegmentRepository.save(segment);
                });
    }
}
