package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.dto.*;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.usecase.*;
import kh.edu.istad.platform.customer.restapi.dto.*;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/customers", "/customers"})
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final QueryCustomerUseCase queryCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final PatchCustomerUseCase patchCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final ActivateCustomerUseCase activateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = customerWebMapper.toDomain(request);
        return createCustomerUseCase.create(customer, request.segmentId());
    }

    @GetMapping
    public Flux<Customer> findAll(CustomerFilterRequest request) {
        if (request != null && hasAnyFilter(request)) {
            return query(request);
        }
        return queryCustomerUseCase.findAll();
    }

    @GetMapping("/{customerId}")
    public Mono<Customer> findById(@PathVariable("customerId") UUID customerId) {
        return queryCustomerUseCase.findById(customerId);
    }

    @GetMapping("/username/{username}")
    public Mono<Customer> findByUsername(@PathVariable("username") String username) {
        return queryCustomerUseCase.findByUsername(username);
    }

    @PutMapping("/{customerId}")
    public Mono<Customer> update(
            @PathVariable("customerId") UUID customerId,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        return updateCustomerUseCase.update(
                customerId,
                request.familyName(),
                request.givenName(),
                request.email(),
                request.phoneNumber(),
                request.segmentId()
        );
    }

    @PatchMapping("/{customerId}")
    public Mono<Customer> patch(
            @PathVariable("customerId") UUID customerId,
            @RequestBody PatchCustomerRequest request
    ) {
        return patchCustomerUseCase.patch(
                customerId,
                request.familyName(),
                request.givenName(),
                request.email(),
                request.phoneNumber(),
                request.segmentId()
        );
    }

    @PatchMapping("/{customerId}/deactivate")
    public Mono<Customer> deactivate(@PathVariable("customerId") UUID customerId) {
        return deactivateCustomerUseCase.deactivate(customerId);
    }

    @PatchMapping("/{customerId}/activate")
    public Mono<Customer> activate(@PathVariable("customerId") UUID customerId) {
        return activateCustomerUseCase.activate(customerId);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("customerId") UUID customerId) {
        return deleteCustomerUseCase.delete(customerId);
    }

    @GetMapping("/segments")
    public Flux<CustomerSegment> findAllSegments() {
        return queryCustomerUseCase.findAllSegments();
    }

    @GetMapping("/query")
    public Flux<Customer> query(CustomerFilterRequest request) {
        CustomerFilterQuery filterQuery = (request != null)
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.query(filterQuery);
    }

    @PostMapping("/query")
    public Flux<Customer> queryWithPost(@RequestBody(required = false) CustomerFilterRequest request) {
        CustomerFilterQuery filterQuery = (request != null)
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.query(filterQuery);
    }

    /**
     * Reports total customers grouped by status and segment.
     */
    @GetMapping({"/statistic", "/statistics"})
    public Flux<CustomerStatistic> getStatistic(CustomerFilterRequest request) {
        CustomerFilterQuery filterQuery = (request != null && hasAnyFilter(request))
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.getStatistic(filterQuery);
    }

    @PostMapping({"/statistic", "/statistics"})
    public Flux<CustomerStatistic> getStatisticWithPost(@RequestBody(required = false) CustomerFilterRequest request) {
        CustomerFilterQuery filterQuery = (request != null && hasAnyFilter(request))
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.getStatistic(filterQuery);
    }

    @GetMapping({"/statistic/summary", "/statistics/summary"})
    public Mono<CustomerSummaryStatistic> getSummaryStatistic() {
        return queryCustomerUseCase.getSummaryStatistic();
    }

    @GetMapping({"/report", "/reports"})
    public Flux<CustomerReport> getReport(CustomerFilterRequest request) {
        CustomerFilterQuery filterQuery = (request != null && hasAnyFilter(request))
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.getReport(filterQuery);
    }

    @PostMapping({"/report", "/reports"})
    public Flux<CustomerReport> getReportWithPost(@RequestBody(required = false) CustomerFilterRequest request) {
        CustomerFilterQuery filterQuery = (request != null && hasAnyFilter(request))
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.getReport(filterQuery);
    }

    private boolean hasAnyFilter(CustomerFilterRequest request) {
        return request.username() != null ||
                request.familyName() != null ||
                request.givenName() != null ||
                request.email() != null ||
                request.phoneNumber() != null ||
                request.status() != null ||
                request.segmentId() != null ||
                request.segmentName() != null ||
                request.keyword() != null ||
                request.page() != null ||
                request.size() != null ||
                request.sortBy() != null;
    }
}
