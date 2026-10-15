package kh.edu.istad.platform.business.persistence.repository;

import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BusinessMongoRepository extends ReactiveMongoRepository<BusinessDocument, String> {
    Mono<Boolean> existsByName(String name);

    Mono<BusinessDocument> findByName(String name);
}
