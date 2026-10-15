package kh.edu.istad.platform.business.restapi.dto;

import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterBusinessResponse {
    private UUID businessId;
    private BusinessStatus status;
    private String message;
}
