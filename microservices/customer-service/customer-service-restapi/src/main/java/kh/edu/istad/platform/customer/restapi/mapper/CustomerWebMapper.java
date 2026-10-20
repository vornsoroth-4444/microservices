package kh.edu.istad.platform.customer.restapi.mapper;

import kh.edu.istad.platform.customer.domain.dto.*;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import kh.edu.istad.platform.customer.restapi.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CustomerWebMapper {

    InitiateCustomerCommand toCommand(CustomerInitiateRequest request);

    CustomerInitiateResponse toResponse(InitiateCustomerResult result);

    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "familyName", source = "request.familyName")
    @Mapping(target = "givenName", source = "request.givenName")
    InitiateUpdateCustomerCommand toUpdateCommand(UUID customerId, CustomerUpdateInitiateRequest request);

    CustomerUpdateInitiateResponse toUpdateResponse(InitiateUpdateCustomerResult result);

    CustomerDeactivateInitiateResponse toDeactivateResponse(InitiateDeactivateCustomerResult result);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "segment.id", source = "segmentId")
    Customer toDomain(CreateCustomerRequest request);

    default Email toEmail(String email) {
        return (email != null && !email.isBlank()) ? new Email(email.trim()) : null;
    }

    default PhoneNumber toPhoneNumber(String phoneNumber) {
        return (phoneNumber != null && !phoneNumber.isBlank()) ? new PhoneNumber(phoneNumber.trim()) : null;
    }

    @Mapping(target = "status", expression = "java(toCustomerStatus(request.status()))")
    CustomerFilterQuery toFilterQuery(CustomerFilterRequest request);

    default CustomerStatus toCustomerStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return CustomerStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
