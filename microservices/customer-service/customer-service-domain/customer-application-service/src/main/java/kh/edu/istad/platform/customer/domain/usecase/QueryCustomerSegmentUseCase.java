package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QueryCustomerSegmentUseCase {

    private final CustomerSegmentRepository customerSegmentRepository;

    public Flux<CustomerSegment> findAll() {
        return customerSegmentRepository.findAll();
    }

    public Mono<CustomerSegment> findById(String id) {
        return customerSegmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                        "Customer segment with id '" + id + "' not found")));
    }

    public Mono<CustomerSegment> findByName(String name) {
        return customerSegmentRepository.findByName(name)
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                        "Customer segment with name '" + name + "' not found")));
    }
}
