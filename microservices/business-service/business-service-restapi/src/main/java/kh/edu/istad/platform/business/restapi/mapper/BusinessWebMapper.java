package kh.edu.istad.platform.business.restapi.mapper;

import kh.edu.istad.platform.business.domain.dto.GetBusinessResult;
import kh.edu.istad.platform.business.domain.dto.RegisterBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.RegisterBusinessResult;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.restapi.dto.*;
import org.springframework.stereotype.Component;

@Component
public class BusinessWebMapper {

    public RegisterBusinessCommand toCommand(RegisterBusinessRequest request) {
        return RegisterBusinessCommand.builder()
                .name(request.getName())
                .address(new BusinessAddress(
                        request.getAddress().getStreet(),
                        request.getAddress().getCity(),
                        request.getAddress().getPostalCode()
                ))
                .contact(new BusinessContact(
                        request.getContact().getEmail(),
                        request.getContact().getPhone()
                ))
                .build();
    }

    public RegisterBusinessResponse toResponse(RegisterBusinessResult result) {
        return RegisterBusinessResponse.builder()
                .businessId(result.businessId())
                .status(result.status())
                .message(result.message())
                .build();
    }

    public BusinessResponse toBusinessResponse(GetBusinessResult result) {
        return BusinessResponse.builder()
                .businessId(result.businessId())
                .name(result.name())
                .status(result.status())
                .address(result.address() != null ? BusinessAddressDto.builder()
                        .street(result.address().street())
                        .city(result.address().city())
                        .postalCode(result.address().postalCode())
                        .build() : null)
                .contact(result.contact() != null ? BusinessContactDto.builder()
                        .email(result.contact().email())
                        .phone(result.contact().phone())
                        .build() : null)
                .build();
    }
}
