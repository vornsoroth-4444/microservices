package kh.edu.istad.platform.customer.persistence.document;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerReportResult {

    @Id
    @Field("_id")
    private CustomerStatus id;

    private CustomerStatus status;

    private Long total;

    private List<CustomerDocument> customers;

    public CustomerStatus getStatus() {
        return status != null ? status : id;
    }

    public void setId(CustomerStatus id) {
        this.id = id;
        if (this.status == null) {
            this.status = id;
        }
    }
}
