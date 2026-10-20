package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.platform.customer.domain.dto.CustomerReport;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.document.CustomerReportResult;
import kh.edu.istad.platform.customer.persistence.document.CustomerSegmentDocument;
import kh.edu.istad.platform.customer.persistence.document.CustomerStatisticResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerPersistenceMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "phoneNumber.value", target = "phoneNumber")
    @Mapping(target = "resolvedSegment", ignore = true)
    CustomerDocument toDocument(Customer customer);

    @Mapping(source = "id", target = "id.value")
    @Mapping(source = "email", target = "email.value")
    @Mapping(source = "phoneNumber", target = "phoneNumber.value")
    Customer toDomain(CustomerDocument customerDocument);

    CustomerSegmentDocument toSegmentDocument(CustomerSegment segment);

    CustomerSegment toSegmentDomain(CustomerSegmentDocument document);

    default CustomerReport toReportDomain(CustomerReportResult reportResult) {
        if (reportResult == null) {
            return null;
        }
        List<Customer> customerList = reportResult.getCustomers() != null
                ? reportResult.getCustomers().stream().map(this::toDomain).toList()
                : List.of();
        return CustomerReport.builder()
                .status(reportResult.getStatus())
                .total(reportResult.getTotal())
                .customers(customerList)
                .build();
    }

    default CustomerStatistic toStatisticDomain(CustomerStatisticResult statisticResult) {
        if (statisticResult == null) {
            return null;
        }
        return CustomerStatistic.builder()
                .status(statisticResult.getStatus())
                .segmentName(statisticResult.getSegmentName())
                .total(statisticResult.getTotal())
                .build();
    }
}
