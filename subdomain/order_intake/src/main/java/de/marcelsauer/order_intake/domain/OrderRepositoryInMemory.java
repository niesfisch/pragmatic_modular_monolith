package de.marcelsauer.order_intake.domain;

import de.marcelsauer.valueobject.OrderId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// reference implementation of the order repository
// could also be used for testing
public class OrderRepositoryInMemory implements OrderRepository {

  private static final Map<UUID, Order> STORE = new HashMap<>();

  @Override
  public Optional<Order> findById(OrderId orderId) {
    return Optional.ofNullable(STORE.get(orderId.asBaseValue()));
  }

  @Override
  public void persist(Order order) {
    STORE.put(order.getId(), order);
  }
}
