package kh.edu.istad.platform.customer.restapi.mapper;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.restapi.dto.CreateCustomerSegmentRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerSegmentWebMapper {

    @Mapping(target = "id", ignore = true)
    CustomerSegment toDomain(CreateCustomerSegmentRequest request);

    CustomerSegmentResponse toResponse(CustomerSegment segment);
}
