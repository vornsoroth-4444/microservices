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

    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final InitiateUpdateCustomerUseCase initiateUpdateCustomerUseCase;
    private final InitiateDeactivateCustomerUseCase initiateDeactivateCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final QueryCustomerUseCase queryCustomerUseCase;

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping
//    public CustomerInitiateResponse initiateCustomer(
//            @RequestBody CustomerInitiateRequest customerInitiateRequest
//    ) {
//
//        InitiateCustomerResult result = initiateCustomerUseCase.execute(
//               customerWebMapper.toCommand(customerInitiateRequest)
//        );
//        return customerWebMapper.toResponse(result);
//    }

    @GetMapping
    public Flux<Customer> findAll(CustomerFilterRequest request){
        if (request != null && hasAnyFilter(request)) {
            return query(request);
        }
        return queryCustomerUseCase.findAll();
    }

    @GetMapping("/segments")
    public Flux<CustomerSegment> findAllSegments() {
        return queryCustomerUseCase.findAllSegments();
    }

    @GetMapping("/query")
    public Flux<Customer> query(CustomerFilterRequest request){
        CustomerFilterQuery filterQuery = (request != null)
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.query(filterQuery);
    }

    @PostMapping("/query")
    public Flux<Customer> queryWithPost(@RequestBody(required = false) CustomerFilterRequest request){
        CustomerFilterQuery filterQuery = (request != null)
                ? customerWebMapper.toFilterQuery(request)
                : CustomerFilterQuery.empty();
        return queryCustomerUseCase.query(filterQuery);
    }

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

    @GetMapping("/{customerId}")
    public Mono<Customer> findById(@PathVariable("customerId") UUID customerId) {
        return queryCustomerUseCase.findById(customerId);
    }

    private boolean hasAnyFilter(CustomerFilterRequest request) {
        return request.username() != null ||
                request.familyName() != null ||
                request.givenName() != null ||
                request.email() != null ||
                request.phoneNumber() != null ||
                request.status() != null ||
                request.keyword() != null ||
                request.page() != null ||
                request.size() != null ||
                request.sortBy() != null;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<Customer> create (@RequestBody CreateCustomerRequest request){
        return createCustomerUseCase.create(customerWebMapper.toDomain(request));
    }

    @PutMapping("/{customerId}")
    public CustomerUpdateInitiateResponse updateCustomer(
            @PathVariable("customerId") UUID customerId,
            @Valid @RequestBody CustomerUpdateInitiateRequest request
    ){
        return customerWebMapper.toUpdateResponse(
                initiateUpdateCustomerUseCase.execute(customerWebMapper.toUpdateCommand(customerId, request))
        );
    }

    @PatchMapping("/{customerId}")
    public CustomerUpdateInitiateResponse patchCustomer(
            @PathVariable("customerId") UUID customerId,
            @Valid @RequestBody CustomerUpdateInitiateRequest request
    ){
        return updateCustomer(customerId, request);
    }

    @PatchMapping("/{customerId}/deactivate")
    public CustomerDeactivateInitiateResponse deactivateInitiateResponse(
            @PathVariable("customerId") UUID customerId
    ){
        return customerWebMapper.toDeactivateResponse(
                initiateDeactivateCustomerUseCase.execute(new InitiateDeactivateCustomerCommand(customerId))
        );
    }

}
