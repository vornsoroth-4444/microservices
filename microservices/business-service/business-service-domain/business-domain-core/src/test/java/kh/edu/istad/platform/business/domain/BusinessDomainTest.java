package kh.edu.istad.platform.business.domain;

import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.OrderApproval;
import kh.edu.istad.platform.business.domain.entity.OrderDetail;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.event.BusinessRegisteredEvent;
import kh.edu.istad.platform.business.domain.event.OrderApprovalApprovedEvent;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.service.BusinessDomainService;
import kh.edu.istad.platform.business.domain.service.BusinessDomainServiceImpl;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessOrderStatus;
import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    @Test
    void testCreateOrderApproval_ValidatesBusinessProductAndOrder() {
        Business business = Business.builder()
                .name("Bakery Shop")
                .address(address)
                .contact(contact)
                .build();
        businessDomainService.validateAndInitiateBusiness(business);

        Product product = Product.builder()
                .businessId(business.getId())
                .name("Bread")
                .price(new Money(new BigDecimal("5.00")))
                .build();
        product.initializeProduct();
        OrderDetail detail = OrderDetail.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .status(BusinessOrderStatus.PENDING)
                .price(new Money(new BigDecimal("5.00")))
                .products(List.of(product))
                .build();

        OrderApproval approval = business.createOrderApproval(detail);

        assertNotNull(approval.getId());
        assertEquals(business.getId(), approval.getBusinessId());
        assertEquals(detail.getId(), approval.getOrderId());
        assertEquals(OrderApprovalStatus.PENDING, approval.getStatus());
        OrderApprovalApprovedEvent event = businessDomainService.approveOrder(approval);
        assertNotNull(event.getCreatedAt());
        assertSame(approval, event.getOrderApproval());
        assertEquals(OrderApprovalStatus.APPROVED, approval.getStatus());
        assertThrows(BusinessDomainException.class, () -> businessDomainService.rejectOrder(approval));
    }

    @Test
    void testCreateOrderApproval_RejectsProductFromAnotherBusiness() {
        Business business = Business.builder()
                .name("Bakery Shop")
                .address(address)
                .contact(contact)
                .build();
        businessDomainService.validateAndInitiateBusiness(business);

        Product product = Product.builder()
                .businessId(new BusinessId(UUID.randomUUID()))
                .name("Bread")
                .price(new Money(new BigDecimal("5.00")))
                .build();
        product.initializeProduct();
        OrderDetail detail = OrderDetail.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .status(BusinessOrderStatus.PENDING)
                .price(new Money(new BigDecimal("5.00")))
                .products(List.of(product))
                .build();

        assertThrows(BusinessDomainException.class, () -> business.createOrderApproval(detail));
    }
}
