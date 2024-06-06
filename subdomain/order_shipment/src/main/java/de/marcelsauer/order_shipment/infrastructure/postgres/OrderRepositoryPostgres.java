package de.marcelsauer.order_shipment.infrastructure.postgres;

import de.marcelsauer.order_shipment.DomainModule;
import de.marcelsauer.order_shipment.domain.Order;
import de.marcelsauer.order_shipment.domain.OrderRepository;
import de.marcelsauer.valueobject.OrderId;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository(DomainModule.PREFIX + "OrderRepositoryPostgres")
public class OrderRepositoryPostgres implements OrderRepository {
  @Override
  public Optional<Order> findById(OrderId orderId) {
    log.info("{}: finding order by id: {}", DomainModule.PREFIX, orderId);
    return null;
  }

  @Override
  public void persist(Order order) {
    log.info("{}: persisting order: {}", DomainModule.PREFIX, order);
  }
}
