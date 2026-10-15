package kh.edu.istad.platform.business.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BusinessDataAccessMapper {

    public BusinessDocument businessToBusinessDocument(Business business) {
        return BusinessDocument.builder()
                .id(business.getId() != null ? business.getId().value().toString() : null)
                .name(business.getName())
                .street(business.getAddress() != null ? business.getAddress().street() : null)
                .city(business.getAddress() != null ? business.getAddress().city() : null)
                .postalCode(business.getAddress() != null ? business.getAddress().postalCode() : null)
                .email(business.getContact() != null ? business.getContact().email() : null)
                .phone(business.getContact() != null ? business.getContact().phone() : null)
                .status(business.getStatus())
                .build();
    }

    public Business businessDocumentToBusiness(BusinessDocument document) {
        return Business.builder()
                .businessId(new BusinessId(UUID.fromString(document.getId())))
                .name(document.getName())
                .address(new BusinessAddress(document.getStreet(), document.getCity(), document.getPostalCode()))
                .contact(new BusinessContact(document.getEmail(), document.getPhone()))
                .status(document.getStatus())
                .build();
    }
}
