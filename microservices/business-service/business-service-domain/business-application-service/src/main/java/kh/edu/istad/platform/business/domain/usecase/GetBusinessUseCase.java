package kh.edu.istad.platform.business.domain.usecase;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.dto.GetBusinessQuery;
import kh.edu.istad.platform.business.domain.dto.GetBusinessResult;
import kh.edu.istad.platform.business.domain.exception.BusinessNotFoundException;
import kh.edu.istad.platform.business.domain.mapper.BusinessDataMapper;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetBusinessUseCase {

    private final BusinessRepository businessRepository;
    private final BusinessDataMapper businessDataMapper;

    public Mono<GetBusinessResult> execute(GetBusinessQuery query) {
        log.info("Fetching business by id: {}", query.businessId());

        return businessRepository.findById(new BusinessId(query.businessId()))
                .switchIfEmpty(Mono.error(new BusinessNotFoundException("Business not found with id: " + query.businessId())))
                .map(businessDataMapper::businessToGetBusinessResult);
    }
}
