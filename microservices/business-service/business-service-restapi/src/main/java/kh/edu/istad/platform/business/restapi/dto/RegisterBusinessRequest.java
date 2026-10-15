package kh.edu.istad.platform.business.restapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterBusinessRequest {

    @NotBlank(message = "Business name is required")
    private String name;

    @NotNull(message = "Business address is required")
    @Valid
    private BusinessAddressRequest address;

    @NotNull(message = "Business contact is required")
    @Valid
    private BusinessContactRequest contact;
}
