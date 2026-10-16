package kh.edu.istad.platform.business.domain.service;

import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.OrderApproval;
import kh.edu.istad.platform.business.domain.event.BusinessDeactivatedEvent;
import kh.edu.istad.platform.business.domain.event.BusinessRegisteredEvent;
import kh.edu.istad.platform.business.domain.event.BusinessUpdatedEvent;
import kh.edu.istad.platform.business.domain.event.OrderApprovalApprovedEvent;
import kh.edu.istad.platform.business.domain.event.OrderApprovalRejectedEvent;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BusinessDomainServiceImpl implements BusinessDomainService {

    private static final String UTC = "UTC";

    @Override
    public BusinessRegisteredEvent validateAndInitiateBusiness(Business business) {
        business.initiateBusiness();
        return new BusinessRegisteredEvent(business, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public BusinessUpdatedEvent updateBusiness(Business business, String name, BusinessAddress address, BusinessContact contact) {
        business.updateDetails(name, address, contact);
        return new BusinessUpdatedEvent(business, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public BusinessDeactivatedEvent deactivateBusiness(Business business) {
        business.deactivate();
        return new BusinessDeactivatedEvent(business, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderApprovalApprovedEvent approveOrder(OrderApproval orderApproval) {
        orderApproval.approve();
        return new OrderApprovalApprovedEvent(orderApproval, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderApprovalRejectedEvent rejectOrder(OrderApproval orderApproval) {
        orderApproval.reject();
        return new OrderApprovalRejectedEvent(orderApproval, ZonedDateTime.now(ZoneId.of(UTC)));
    }
}
