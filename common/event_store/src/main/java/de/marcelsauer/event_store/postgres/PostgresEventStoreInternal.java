package de.marcelsauer.event_store.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.PgUtil;
import de.marcelsauer.encryption.EncryptionService;
import de.marcelsauer.event.internal.DomainEvent;
import de.marcelsauer.event_store.EventStoreInternal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class PostgresEventStoreInternal implements EventStoreInternal {

  private final ObjectMapper objectMapper;
  private final JdbcTemplate jdbcTemplate;
  private final String tableName;
  private final EncryptionService encryptionService;
  private AtomicLong eventCounter = new AtomicLong();
  private AtomicLong archiveCounter = new AtomicLong();

  @Override
  public void store(DomainEvent event) {
    String plaintextJson = toJson(event);
    String encryptedJson = encryptionService.encrypt(plaintextJson);
    jdbcTemplate.update(
        "INSERT INTO %s (id, aggregate_id, occurred_at, event_type, payload) VALUES (?, ?, ?, ?, ?)"
            .formatted(tableName),
        event.getId(),
        event.getAggregateId(),
        PgUtil.toTimestamp(event.getOccurredAt()),
        event.getClass().getName(),
        encryptedJson);
    eventCounter.incrementAndGet();
  }

  @Override
  public boolean exists(UUID id) {
    return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM %s WHERE id = ?".formatted(tableName), Integer.class, id)
        > 0;
  }

  @Override
  public List<DomainEvent> findAll(int limit) {
    return jdbcTemplate.query(
        "SELECT id, aggregate_id, occurred_at, event_type, payload FROM %s limit %s"
            .formatted(tableName, limit),
        (rs, rowNum) -> {
          String encryptedJson = rs.getString("payload");
          String plaintextJson = encryptionService.decrypt(encryptedJson);
          try {
            return (DomainEvent)
                objectMapper.readValue(plaintextJson, Class.forName(rs.getString("event_type")));
          } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public void archive(Collection<DomainEvent> events) {
    if (events.isEmpty()) {
      return;
    }
    events.forEach(
        event -> {
          String plaintextJson = toJson(event);
          String encryptedJson = encryptionService.encrypt(plaintextJson);
          String sql =
              """
                  INSERT INTO %s_archive
                  (id, aggregate_id, occurred_at, event_type, payload, archived_at)
                  VALUES
                  (?, ?, ?, ?, ?, ?)
                  ON CONFLICT (id) DO NOTHING
              """
                  .formatted(tableName);
          jdbcTemplate.update(
              sql,
              event.getId(),
              event.getAggregateId(),
              PgUtil.toTimestamp(event.getOccurredAt()),
              event.getClass().getName(),
              encryptedJson,
              PgUtil.toTimestamp(Instant.now()));
        });
    String eventIds =
        events.stream()
            .map(DomainEvent::getId)
            .map(uuid -> "'%s'".formatted(uuid.toString()))
            .collect(Collectors.joining(","));
    int update =
        jdbcTemplate.update("DELETE FROM %s WHERE id IN (%s)".formatted(tableName, eventIds));

    archiveCounter.addAndGet(update);
  }

  @Scheduled(fixedRate = 5_000)
  public void printStats() {
    log.info(">> internal events stored since restart: {}", eventCounter.get());
    log.info(">> internal events archived since restart: {}", archiveCounter.get());
  }

  private String toJson(DomainEvent event) {
    try {
      return objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
