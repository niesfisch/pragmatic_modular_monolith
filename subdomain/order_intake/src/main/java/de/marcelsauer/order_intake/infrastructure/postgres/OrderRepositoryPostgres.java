package de.marcelsauer.order_intake.infrastructure.postgres;

import de.marcelsauer.PgUtil;
import de.marcelsauer.encryption.EncryptionService;
import de.marcelsauer.order_intake.DomainModule;
import de.marcelsauer.order_intake.domain.Order;
import de.marcelsauer.order_intake.domain.OrderRepository;
import de.marcelsauer.valueobject.OrderId;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Slf4j
@Repository(DomainModule.PREFIX + "OrderRepositoryPostgres")
public class OrderRepositoryPostgres implements OrderRepository {

  private final JdbcTemplate jdbcTemplate;
  private final EncryptionService encryptionService;

  // this could also be mappend from and to an entity
  @Override
  public Optional<Order> findById(OrderId orderId) {

    try {
      return Optional.of(
          jdbcTemplate.queryForObject(
              "SELECT * FROM order_intake__order WHERE id = ?",
              (rs, rowNum) ->
                  new Order(
                      encryptionService.decrypt(rs.getString("firstname")),
                      encryptionService.decrypt(rs.getString("lastname")),
                      rs.getInt("article_nr"),
                      rs.getString("external_id")),
              orderId.asBaseValue()));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  // this could also be mappend from and to an entity
  @Override
  public void persist(Order order) {
    jdbcTemplate.update(
        "INSERT INTO order_intake__order (id, firstname, lastname, article_nr, external_id,"
            + " created) VALUES (?, ?, ?, ?, ?, ?)",
        order.getId(),
        encryptionService.encrypt(order.getFirstname()),
        encryptionService.encrypt(order.getLastname()),
        order.getArticleNr(),
        order.getExternalId(),
        PgUtil.toTimestamp(Instant.now()));
  }
}
