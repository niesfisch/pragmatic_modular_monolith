package de.marcelsauer.event_store;

import de.marcelsauer.event.internal.DomainEvent;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EventStoreInternal {
  void store(DomainEvent event);

  boolean exists(UUID id);

  List<DomainEvent> findAll(int limit);

  void archive(Collection<DomainEvent> events);
}
