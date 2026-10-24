package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.platform.order.domain.dto.client.BusinessClientModel;

import java.util.Optional;
import java.util.UUID;

public interface BusinessClientPort {
    Optional<BusinessClientModel> getBusiness(UUID businessId);
}
