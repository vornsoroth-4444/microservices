package kh.edu.istad.platform.customer.persistence.config;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import kh.edu.istad.platform.customer.persistence.document.CustomerSegmentDocument;
import kh.edu.istad.platform.customer.persistence.repository.CustomerSegmentMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerSegmentDataInitializer implements CommandLineRunner {

    private final CustomerSegmentMongoRepository customerSegmentMongoRepository;

    @Override
    public void run(String... args) {
        customerSegmentMongoRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> {
                    log.info("Initializing default CustomerSegments (Silver, Gold, Premium)...");
                    return customerSegmentMongoRepository.saveAll(List.of(
                            CustomerSegmentDocument.builder()
                                    .name("Silver")
                                    .type(CustomerSegmentType.SILVER)
                                    .description("Silver tier customer segment")
                                    .build(),
                            CustomerSegmentDocument.builder()
                                    .name("Gold")
                                    .type(CustomerSegmentType.GOLD)
                                    .description("Gold tier customer segment")
                                    .build(),
                            CustomerSegmentDocument.builder()
                                    .name("Premium")
                                    .type(CustomerSegmentType.PREMIUM)
                                    .description("Premium tier customer segment")
                                    .build()
                    ));
                })
                .doOnNext(segment -> log.info("Seeded CustomerSegment: {} with ID: {}", segment.getName(), segment.getId()))
                .subscribe();
    }
}
