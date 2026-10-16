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

public interface BusinessDomainService {
    BusinessRegisteredEvent validateAndInitiateBusiness(Business business);

    BusinessUpdatedEvent updateBusiness(Business business, String name, BusinessAddress address, BusinessContact contact);

    BusinessDeactivatedEvent deactivateBusiness(Business business);

    OrderApprovalApprovedEvent approveOrder(OrderApproval orderApproval);

    OrderApprovalRejectedEvent rejectOrder(OrderApproval orderApproval);
}
