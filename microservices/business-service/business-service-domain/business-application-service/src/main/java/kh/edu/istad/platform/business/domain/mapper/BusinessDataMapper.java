package kh.edu.istad.platform.business.domain.mapper;

import kh.edu.istad.platform.business.domain.dto.GetBusinessResult;
import kh.edu.istad.platform.business.domain.dto.RegisterBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.RegisterBusinessResult;
import kh.edu.istad.platform.business.domain.entity.Business;
import org.springframework.stereotype.Component;

@Component
public class BusinessDataMapper {

    public Business registerBusinessCommandToBusiness(RegisterBusinessCommand command) {
        return Business.builder()
                .name(command.name())
                .address(command.address())
                .contact(command.contact())
                .build();
    }

    public RegisterBusinessResult businessToRegisterBusinessResult(Business business, String message) {
        return RegisterBusinessResult.builder()
                .businessId(business.getId().value())
                .status(business.getStatus())
                .message(message)
                .build();
    }

    public GetBusinessResult businessToGetBusinessResult(Business business) {
        return GetBusinessResult.builder()
                .businessId(business.getId().value())
                .name(business.getName())
                .address(business.getAddress())
                .contact(business.getContact())
                .status(business.getStatus())
                .build();
    }
}
