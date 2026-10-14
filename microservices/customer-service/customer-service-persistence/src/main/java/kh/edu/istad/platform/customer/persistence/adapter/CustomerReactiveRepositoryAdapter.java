package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;

    public CustomerReactiveRepositoryAdapter(CustomerMongoRepository customerMongoRepository) {
        this.customerMongoRepository = customerMongoRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerDocument customerDocument = CustomerDocument.builder()
                .id(new CustomerId(UUID.randomUUID()))
                .username("vorsoroth")
                .build();

        customerMongoRepository.save(customerDocument).block();
        return null;
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return Optional.empty();
    }
}
