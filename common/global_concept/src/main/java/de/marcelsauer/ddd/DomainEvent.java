package de.marcelsauer.ddd;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DomainEvent {
  private final UUID id;
  private final String name;
  private final Instant occurredOn;
}
