package kh.edu.istad.platform.business.restapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessContactDto {
    private String email;
    private String phone;
}
