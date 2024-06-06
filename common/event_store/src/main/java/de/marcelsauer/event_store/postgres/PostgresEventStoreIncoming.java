package de.marcelsauer.event_store.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.encryption.EncryptionService;
import de.marcelsauer.event_store.EventStoreIncoming;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class PostgresEventStoreIncoming implements EventStoreIncoming {

  private final ObjectMapper objectMapper;
  private final JdbcTemplate jdbcTemplate;
  private final String tableName;
  private final EncryptionService encryptionService;
  private final AtomicLong eventCounter = new AtomicLong();

  @Override
  public void store(IncomingEvent incomingEvent) {
    String plaintextJson = toJson(incomingEvent);
    String encryptedJson = encryptionService.encrypt(plaintextJson);
    jdbcTemplate.update(
        "INSERT INTO %s (id, payload) VALUES (?, ?) ON CONFLICT (id) DO NOTHING"
            .formatted(tableName),
        incomingEvent.id(),
        encryptedJson);
    eventCounter.incrementAndGet();
  }

  private String toJson(IncomingEvent incomingEvent) {
    try {
      return objectMapper.writeValueAsString(incomingEvent);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean exists(String eventId) {
    return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM %s WHERE id = ?".formatted(tableName), Integer.class, eventId)
        > 0;
  }

  @Override
  public boolean exists(UUID eventId) {
    return exists(eventId.toString());
  }

  @Scheduled(fixedRate = 5_000)
  public void printStats() {
    log.info("> incoming events stored since restart: {}", eventCounter.get());
  }
}
