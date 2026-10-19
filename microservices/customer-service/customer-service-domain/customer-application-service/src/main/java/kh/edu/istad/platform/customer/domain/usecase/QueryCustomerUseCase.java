package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilterQuery;
import kh.edu.istad.platform.customer.domain.dto.CustomerReport;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.dto.CustomerSummaryStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryCustomerUseCase {
    private final CustomerRepository customerRepository;

    public Flux<Customer> findAll(){
        return customerRepository.findAll();
    }

    public Mono<Customer> findById(UUID id) {
        return customerRepository.findById(new CustomerId(id));
    }

    public Flux<Customer> query(){
        return customerRepository.query();
    }

    public Flux<Customer> query(CustomerFilterQuery filterQuery){
        return customerRepository.query(filterQuery);
    }

    public Flux<CustomerSegment> findAllSegments(){
        return customerRepository.findAllSegments();
    }

    public Flux<CustomerStatistic> getStatistic() {
        return customerRepository.getStatistic();
    }

    public Flux<CustomerStatistic> getStatistic(CustomerFilterQuery filterQuery) {
        return customerRepository.getStatistic(filterQuery);
    }

    public Flux<CustomerReport> getReport() {
        return customerRepository.getReport();
    }

    public Flux<CustomerReport> getReport(CustomerFilterQuery filterQuery) {
        return customerRepository.getReport(filterQuery);
    }

    public Mono<CustomerSummaryStatistic> getSummaryStatistic() {
        return customerRepository.getSummaryStatistic();
    }
}
