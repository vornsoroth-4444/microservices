package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.platform.order.domain.dto.TrackOrderQuery;
import kh.edu.istad.platform.order.domain.dto.TrackOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.exception.OrderNotFoundException;
import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderDataMapper orderDataMapper;

    @Transactional(readOnly = true)
    public TrackOrderResult execute(TrackOrderQuery query) {
        log.info("Tracking order with tracking id: {}", query.orderTrackingId());

        Order order = orderRepository.findByTrackingId(new TrackingId(query.orderTrackingId()))
                .orElseThrow(() -> {
                    log.warn("Could not find order with tracking id: {}", query.orderTrackingId());
                    return new OrderNotFoundException("Could not find order with tracking id: " + query.orderTrackingId());
                });

        return orderDataMapper.orderToTrackOrderResult(order);
    }
}
