package kh.edu.istad.platform.business.domain.usecase;

import kh.edu.istad.platform.business.domain.dto.RegisterBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.RegisterBusinessResult;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.event.BusinessRegisteredEvent;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.mapper.BusinessDataMapper;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.service.BusinessDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterBusinessUseCase {

    private final BusinessDomainService businessDomainService;
    private final BusinessRepository businessRepository;
    private final BusinessDataMapper businessDataMapper;

    public Mono<RegisterBusinessResult> execute(RegisterBusinessCommand command) {
        log.info("Processing business registration for: {}", command.name());

        return businessRepository.existsByName(command.name())
                .defaultIfEmpty(false)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("Business registration failed: Business with name '{}' already exists", command.name());
                        return Mono.error(new BusinessDomainException("Business with name '" + command.name() + "' already exists!"));
                    }

                    Business business = businessDataMapper.registerBusinessCommandToBusiness(command);
                    BusinessRegisteredEvent event = businessDomainService.validateAndInitiateBusiness(business);
                    log.info("Business domain entity initiated with id: {}", business.getId().value());

                    return businessRepository.save(business)
                            .map(savedBusiness -> {
                                log.info("Business successfully saved with id: {}", savedBusiness.getId().value());
                                return businessDataMapper.businessToRegisterBusinessResult(
                                        savedBusiness, "Business registered successfully"
                                );
                            });
                });
    }
}
