package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CustomerRepository {
    Mono<Customer> save(Customer customer);

    Flux<Customer> findAll();

    Mono<Customer> findById(CustomerId id);

    Mono<Void> DeleteById(CustomerId id);

    Mono<Customer> findByUserName(String username);

    Flux<Customer> query();
}
