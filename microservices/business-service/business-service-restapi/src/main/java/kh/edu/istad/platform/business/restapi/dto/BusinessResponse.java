package kh.edu.istad.platform.business.restapi.dto;

import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessResponse {
    private UUID businessId;
    private String name;
    private BusinessStatus status;
    private BusinessAddressDto address;
    private BusinessContactDto contact;
}
