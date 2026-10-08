package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.InitiateUpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateUpdateCustomerResult;
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
public class InitiateUpdateCustomerUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;

    public InitiateUpdateCustomerResult execute(InitiateUpdateCustomerCommand command) {
        log.info("initiate update customer usecase: {}", command);
        return new InitiateUpdateCustomerResult(
                command.customerId(),
                command.familyName(),
                command.givenName(),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }
}
