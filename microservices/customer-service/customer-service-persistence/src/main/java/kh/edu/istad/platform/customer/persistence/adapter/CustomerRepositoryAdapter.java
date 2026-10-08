package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.entity.CustomerEntity;
import kh.edu.istad.platform.customer.persistence.repository.CustomerJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private CustomerJpaRepository customerJpaRepository;

    @Override
    public Customer save(Customer customer) {
        customerJpaRepository.save(new CustomerEntity());
        return null;
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return Optional.empty();
    }
}
