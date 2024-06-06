package de.marcelsauer.order_intake.domain;

import de.marcelsauer.valueobject.OrderId;
import java.util.Optional;

public interface OrderRepository {
  Optional<Order> findById(OrderId orderId);

  void persist(Order order);
}
