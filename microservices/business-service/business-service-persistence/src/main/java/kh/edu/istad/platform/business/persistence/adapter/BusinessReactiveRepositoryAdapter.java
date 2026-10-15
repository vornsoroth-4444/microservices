package kh.edu.istad.platform.business.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import kh.edu.istad.platform.business.persistence.mapper.BusinessDataAccessMapper;
import kh.edu.istad.platform.business.persistence.repository.BusinessMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
                .map(businessDataAccessMapper::businessDocumentToBusiness);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return businessMongoRepository.existsByName(name);
    }
}
