package de.marcelsauer.order_intake.infrastructure.postgres;

import static org.assertj.core.api.Assertions.assertThat;

import de.marcelsauer.order_intake.domain.Order;
import de.marcelsauer.valueobject.OrderId;
import java.util.Optional;
import java.util.UUID;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class OrderRepositoryPostgresIT {

  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private OrderRepositoryPostgres orderRepository;
  private final Faker faker = new Faker();

  @BeforeEach
  void setUp() {
    jdbcTemplate.execute("TRUNCATE TABLE order_intake__order");
  }

  @Test
  void shouldPersistAndFind() {
    // given
    String externalId = faker.internet().uuid();
    OrderId orderId = new OrderId(UUID.fromString(externalId));
    Order order =
        new Order(
            faker.name().firstName(),
            faker.name().lastName(),
            faker.number().numberBetween(1, 100),
            externalId);

    // when
    orderRepository.persist(order);
    Optional<Order> result = orderRepository.findById(orderId);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(order);
  }
}
