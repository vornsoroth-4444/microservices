package kh.edu.istad.platform.business.persistence.document;

import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "businesses")
public class BusinessDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String street;
    private String city;
    private String postalCode;

    private String email;
    private String phone;

    private BusinessStatus status;
}
