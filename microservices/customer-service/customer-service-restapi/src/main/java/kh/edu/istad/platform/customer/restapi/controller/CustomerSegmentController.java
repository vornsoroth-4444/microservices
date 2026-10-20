package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.DeleteCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.restapi.dto.CreateCustomerSegmentRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentResponse;
import kh.edu.istad.platform.customer.restapi.dto.UpdateCustomerSegmentRequest;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerSegmentWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/customer-segments", "/api/segments", "/customer-segments"})
public class CustomerSegmentController {

    private final CreateCustomerSegmentUseCase createCustomerSegmentUseCase;
    private final QueryCustomerSegmentUseCase queryCustomerSegmentUseCase;
    private final UpdateCustomerSegmentUseCase updateCustomerSegmentUseCase;
    private final DeleteCustomerSegmentUseCase deleteCustomerSegmentUseCase;
    private final CustomerSegmentWebMapper customerSegmentWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerSegmentResponse> create(@Valid @RequestBody CreateCustomerSegmentRequest request) {
        CustomerSegment segment = customerSegmentWebMapper.toDomain(request);
        return createCustomerSegmentUseCase.create(segment)
                .map(customerSegmentWebMapper::toResponse);
    }

    @GetMapping
    public Flux<CustomerSegmentResponse> findAll() {
        return queryCustomerSegmentUseCase.findAll()
                .map(customerSegmentWebMapper::toResponse);
    }

    @GetMapping("/{segmentId}")
    public Mono<CustomerSegmentResponse> findById(@PathVariable("segmentId") String segmentId) {
        return queryCustomerSegmentUseCase.findById(segmentId)
                .map(customerSegmentWebMapper::toResponse);
    }

    @PutMapping("/{segmentId}")
    public Mono<CustomerSegmentResponse> update(
            @PathVariable("segmentId") String segmentId,
            @Valid @RequestBody UpdateCustomerSegmentRequest request
    ) {
        return updateCustomerSegmentUseCase.update(
                        segmentId,
                        request.name(),
                        request.type(),
                        request.description()
                )
                .map(customerSegmentWebMapper::toResponse);
    }

    @DeleteMapping("/{segmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("segmentId") String segmentId) {
        return deleteCustomerSegmentUseCase.delete(segmentId);
    }
}
