package kh.edu.istad.platform.business.domain;

import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.event.BusinessRegisteredEvent;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.service.BusinessDomainService;
import kh.edu.istad.platform.business.domain.service.BusinessDomainServiceImpl;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessDomainTest {

    private BusinessDomainService businessDomainService;
    private BusinessAddress address;
    private BusinessContact contact;

    @BeforeEach
    void setUp() {
        businessDomainService = new BusinessDomainServiceImpl();
        address = new BusinessAddress("Street 2004", "Phnom Penh", "12000");
        contact = new BusinessContact("contact@business.com", "+85512345678");
    }

    @Test
    void testValidateAndInitiateBusiness_Success() {
        Business business = Business.builder()
                .name("Coffee Central")
                .address(address)
                .contact(contact)
                .build();

        BusinessRegisteredEvent event = businessDomainService.validateAndInitiateBusiness(business);

        assertNotNull(event);
        assertNotNull(business.getId());
        assertEquals(BusinessStatus.ACTIVE, business.getStatus());
        assertEquals("Coffee Central", business.getName());
    }

    @Test
    void testValidateAndInitiateBusiness_ThrowsException_WhenNameIsBlank() {
        Business business = Business.builder()
                .name("   ")
                .address(address)
                .contact(contact)
                .build();

        assertThrows(BusinessDomainException.class, () -> businessDomainService.validateAndInitiateBusiness(business));
    }

    @Test
    void testValidateAndInitiateBusiness_ThrowsException_WhenAddressIsNull() {
        Business business = Business.builder()
                .name("Coffee Central")
                .address(null)
                .contact(contact)
                .build();

        assertThrows(BusinessDomainException.class, () -> businessDomainService.validateAndInitiateBusiness(business));
    }

    @Test
    void testBusinessLifecycle_DeactivateAndActivate() {
        Business business = Business.builder()
                .name("Bakery Shop")
                .address(address)
                .contact(contact)
                .build();

        businessDomainService.validateAndInitiateBusiness(business);
        assertEquals(BusinessStatus.ACTIVE, business.getStatus());

        businessDomainService.deactivateBusiness(business);
        assertEquals(BusinessStatus.INACTIVE, business.getStatus());

        business.activate();
        assertEquals(BusinessStatus.ACTIVE, business.getStatus());
    }
}
