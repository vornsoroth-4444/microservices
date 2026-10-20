package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.persistence.document.CustomerSegmentDocument;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerSegmentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomerSegmentReactiveRepositoryAdapter implements CustomerSegmentRepository {

    private final CustomerSegmentMongoRepository customerSegmentMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<CustomerSegment> save(CustomerSegment segment) {
        CustomerSegmentDocument doc = customerPersistenceMapper.toSegmentDocument(segment);
        return customerSegmentMongoRepository.save(doc)
                .map(customerPersistenceMapper::toSegmentDomain);
    }

    @Override
    public Mono<CustomerSegment> findById(String id) {
        return customerSegmentMongoRepository.findById(id)
                .map(customerPersistenceMapper::toSegmentDomain);
    }

    @Override
    public Mono<CustomerSegment> findByName(String name) {
        return customerSegmentMongoRepository.findByName(name)
                .map(customerPersistenceMapper::toSegmentDomain);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return customerSegmentMongoRepository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return customerSegmentMongoRepository.existsByName(name);
    }

    @Override
    public Flux<CustomerSegment> findAll() {
        return customerSegmentMongoRepository.findAll()
                .map(customerPersistenceMapper::toSegmentDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return customerSegmentMongoRepository.deleteById(id);
    }

    @Override
    public Mono<Long> countCustomersBySegmentId(String segmentId) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("segment").is(segmentId),
                Criteria.where("segment.$id").is(segmentId)
        );
        return reactiveMongoTemplate.count(Query.query(criteria), "customers");
    }
}
