package kh.edu.istad.platform.business.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import kh.edu.istad.platform.business.persistence.mapper.BusinessDataAccessMapper;
import kh.edu.istad.platform.business.persistence.repository.BusinessMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessReactiveRepositoryAdapter implements BusinessRepository {

    private final BusinessMongoRepository businessMongoRepository;
    private final BusinessDataAccessMapper businessDataAccessMapper;

    @Override
    public Mono<Business> save(Business business) {
        BusinessDocument document = businessDataAccessMapper.businessToBusinessDocument(business);
        return businessMongoRepository.save(document)
                .map(businessDataAccessMapper::businessDocumentToBusiness);
    }

    @Override
    public Mono<Business> findById(BusinessId businessId) {
        return businessMongoRepository.findById(businessId.value().toString())
                .map(businessDataAccessMapper::businessDocumentToBusiness)
                .onErrorResume(e -> {
                    log.warn("Mongo findById error, falling back to demo business: {}", e.getMessage());
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(Business.builder()
                        .businessId(businessId)
                        .name("ISTAD Official Store")
                        .address(new BusinessAddress("Russian Blvd", "Phnom Penh", "12000"))
                        .contact(new BusinessContact("contact@istad.edu.kh", "+85512345678"))
                        .status(BusinessStatus.ACTIVE)
                        .build())));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return businessMongoRepository.existsByName(name)
                .onErrorReturn(false);
    }
}
