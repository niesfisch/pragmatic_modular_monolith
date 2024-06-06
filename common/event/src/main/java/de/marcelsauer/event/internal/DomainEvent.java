package de.marcelsauer.event.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public abstract class DomainEvent {
  private UUID id;
  private UUID aggregateId;
  private Instant occurredAt;

  @JsonIgnore
  public abstract int getVersion();

  // for json
  public DomainEvent() {}

  public DomainEvent(UUID aggregateId) {
    this.id = UUID.randomUUID();
    this.occurredAt = Instant.now();
    this.aggregateId = aggregateId;
  }

  // e.g. de.marcelsauer.event.OrderOverdueEvent.v2
  @JsonIgnore
  public String fqName() {
    return VersionCleaner.clean(this.getClass().getName()) + ".v" + getVersion();
  }
}
