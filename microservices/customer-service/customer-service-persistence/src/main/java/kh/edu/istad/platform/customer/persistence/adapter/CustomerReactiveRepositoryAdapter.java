package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public CustomerReactiveRepositoryAdapter(CustomerMongoRepository customerMongoRepository, CustomerPersistenceMapper customerPersistenceMapper, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.customerMongoRepository = customerMongoRepository;
        this.customerPersistenceMapper = customerPersistenceMapper;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Customer> save(Customer customer) {
      return   customerMongoRepository.save(
                customerPersistenceMapper.toDocument(customer)
        ).map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> findAll() {
        return customerMongoRepository.findAll()
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Customer> findById(CustomerId id) {
        return customerMongoRepository.findById(id.value().toString()).map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> DeleteById(CustomerId id) {
        return customerMongoRepository.deleteById(id.value().toString());
    }

    @Override
    public Mono<Customer> findByUserName(String username) {
        return customerMongoRepository.findByUsername(username)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> query() {
        Query query = Query.query(
                Criteria.where("username")
                        .regex("ka","i")
        );
        return reactiveMongoTemplate.find(query, CustomerDocument.class).map(
                customerPersistenceMapper::toDomain
        );
    }


}
