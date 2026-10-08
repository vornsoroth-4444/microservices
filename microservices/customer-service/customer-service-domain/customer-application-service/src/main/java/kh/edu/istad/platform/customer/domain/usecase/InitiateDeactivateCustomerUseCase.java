package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.InitiateDeactivateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateDeactivateCustomerResult;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitiateDeactivateCustomerUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;

    public InitiateDeactivateCustomerResult execute(InitiateDeactivateCustomerCommand command) {
        log.info("initiate deactivate customer usecase: {}", command);
        return new InitiateDeactivateCustomerResult(
                command.customerId(),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }
}
