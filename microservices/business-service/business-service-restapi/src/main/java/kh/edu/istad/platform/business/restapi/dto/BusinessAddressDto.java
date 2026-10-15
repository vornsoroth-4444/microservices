package kh.edu.istad.platform.business.restapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessAddressDto {
    private String street;
    private String city;
    private String postalCode;
}
