package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.InitiateDeactivateCustomerCommand;
import kh.edu.istad.platform.customer.domain.entity.Customer;
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
@RequestMapping("/api/customers")
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
    public Flux<Customer> findAll(){
        return queryCustomerUseCase.findAll();
    }

    @GetMapping("/query")
    public Flux<Customer> query(){
        return queryCustomerUseCase.query();
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
