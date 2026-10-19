package kh.edu.istad.platform.customer.persistence.document;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerStatisticResult {

    @Id
    @Field("_id")
    private CustomerStatus id;

    private CustomerStatus status;

    private Long total;

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
