package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilterQuery;
import kh.edu.istad.platform.customer.domain.dto.CustomerReport;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.dto.CustomerSummaryStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
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

    Flux<Customer> query(CustomerFilterQuery filterQuery);

    Flux<CustomerSegment> findAllSegments();

    Flux<CustomerStatistic> getStatistic();

    Flux<CustomerStatistic> getStatistic(CustomerFilterQuery filterQuery);

    Flux<CustomerReport> getReport();

    Flux<CustomerReport> getReport(CustomerFilterQuery filterQuery);

    Mono<CustomerSummaryStatistic> getSummaryStatistic();
}

