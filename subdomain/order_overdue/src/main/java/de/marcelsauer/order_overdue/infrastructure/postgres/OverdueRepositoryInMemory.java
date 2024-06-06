package de.marcelsauer.order_overdue.infrastructure.postgres;

import de.marcelsauer.order_overdue.domain.Order;
import de.marcelsauer.order_overdue.domain.OverdueRepository;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OverdueRepositoryInMemory implements OverdueRepository {

  @Override
  public Collection<Order> findAllOverdueOrders() {
    return List.of(new Order(), new Order(), new Order());
  }
}
