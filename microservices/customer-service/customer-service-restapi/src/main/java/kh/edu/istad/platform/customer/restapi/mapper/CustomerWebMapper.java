package kh.edu.istad.platform.customer.restapi.mapper;

import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.InitiateDeactivateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.InitiateUpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateUpdateCustomerResult;
import kh.edu.istad.platform.customer.restapi.dto.CustomerDeactivateInitiateResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerUpdateInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerUpdateInitiateResponse;
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

}
