package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteCustomerSegmentUseCase {

    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<Void> delete(String id) {
        log.info("Deleting customer segment id: {}", id);

        return customerSegmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                        "Customer segment with id '" + id + "' not found")))
                .flatMap(segment -> customerSegmentRepository.countCustomersBySegmentId(id)
                        .flatMap(count -> {
                            if (count != null && count > 0) {
                                return Mono.error(new CustomerDomainException(
                                        "Cannot delete customer segment because " + count + " customer(s) are currently assigned to it"));
                            }
                            return customerSegmentRepository.deleteById(id);
                        }));
    }
}
