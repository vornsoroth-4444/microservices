package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerSegmentRepository {
    Mono<CustomerSegment> save(CustomerSegment segment);

    Mono<CustomerSegment> findById(String id);

    Mono<CustomerSegment> findByName(String name);

    Mono<Boolean> existsById(String id);

    Mono<Boolean> existsByName(String name);

    Flux<CustomerSegment> findAll();

    Mono<Void> deleteById(String id);

    Mono<Long> countCustomersBySegmentId(String segmentId);
}
