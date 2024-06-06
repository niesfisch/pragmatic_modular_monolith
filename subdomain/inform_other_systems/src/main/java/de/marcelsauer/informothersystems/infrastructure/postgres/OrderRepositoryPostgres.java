package de.marcelsauer.informothersystems.infrastructure.postgres;

import de.marcelsauer.informothersystems.DomainModule;
import de.marcelsauer.informothersystems.domain.Order;
import de.marcelsauer.informothersystems.domain.OrderRepository;
import de.marcelsauer.valueobject.OrderId;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository(DomainModule.PREFIX + "OrderRepositoryPostgres")
public class OrderRepositoryPostgres implements OrderRepository {
  private final Faker faker = new Faker();

  @Override
  public Optional<Order> findById(OrderId orderId) {
    log.info("{}: finding order by id: {}", DomainModule.PREFIX, orderId);
    return Optional.empty();
  }

  @Override
  public void persist(Order order) {
    log.info("{}: persisting order: {}", DomainModule.PREFIX, order);
  }

  @Override
  public long countAll() {
    return faker.random().nextLong(1, 9999999);
  }
}
