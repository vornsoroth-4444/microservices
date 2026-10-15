package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.mapper.OrderDataAccessMapper;
import kh.edu.istad.platform.order.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    @Override
    public Order save(Order order) {
        Optional<OrderEntity> existingEntityOpt = orderJpaRepository.findById(order.getId().value());
        if (existingEntityOpt.isEmpty()) {
            OrderEntity orderEntity = orderDataAccessMapper.orderToOrderEntity(order);
            OrderEntity savedEntity = orderJpaRepository.save(orderEntity);
            return orderDataAccessMapper.orderEntityToOrder(savedEntity);
        } else {
            OrderEntity existingEntity = existingEntityOpt.get();
            existingEntity.setOrderStatus(order.getOrderStatus());
            if (order.getFailureMessages() != null) {
                existingEntity.setFailureMessages(String.join(",", order.getFailureMessages()));
            }
            OrderEntity savedEntity = orderJpaRepository.save(existingEntity);
            return orderDataAccessMapper.orderEntityToOrder(savedEntity);
        }
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository.findById(orderId.value())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.value())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
