package kh.edu.istad.platform.business.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.business.domain.dto.GetBusinessQuery;
import kh.edu.istad.platform.business.domain.usecase.GetBusinessUseCase;
import kh.edu.istad.platform.business.domain.usecase.RegisterBusinessUseCase;
import kh.edu.istad.platform.business.restapi.dto.BusinessResponse;
import kh.edu.istad.platform.business.restapi.dto.RegisterBusinessRequest;
import kh.edu.istad.platform.business.restapi.dto.RegisterBusinessResponse;
import kh.edu.istad.platform.business.restapi.mapper.BusinessWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/businesses")
public class BusinessController {

    private final RegisterBusinessUseCase registerBusinessUseCase;
    private final GetBusinessUseCase getBusinessUseCase;
    private final BusinessWebMapper businessWebMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<RegisterBusinessResponse> registerBusiness(@Valid @RequestBody RegisterBusinessRequest request) {
        log.info("Received reactive request to register business: {}", request.getName());
        return registerBusinessUseCase.execute(businessWebMapper.toCommand(request))
                .map(businessWebMapper::toResponse);
    }

    @GetMapping("/{businessId}")
    public Mono<BusinessResponse> getBusiness(@PathVariable("businessId") UUID businessId) {
        log.info("Received reactive request to get business: {}", businessId);
        return getBusinessUseCase.execute(new GetBusinessQuery(businessId))
                .map(businessWebMapper::toBusinessResponse);
    }
}
