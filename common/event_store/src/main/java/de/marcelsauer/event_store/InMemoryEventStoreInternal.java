package de.marcelsauer.event_store;

import de.marcelsauer.event.internal.DomainEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InMemoryEventStoreInternal implements EventStoreInternal {
  private static final Map<String, DomainEvent> events = new ConcurrentHashMap<>();

  @Override
  public void store(DomainEvent event) {
    log.info("storing event: {}", event);
    events.put(event.toString(), event);
  }

  @Override
  public boolean exists(UUID id) {
    log.info("checking if event exists {}", id);
    return events.containsKey(id.toString());
  }

  @Override
  public List<DomainEvent> findAll(int limit) {
    return List.copyOf(events.values());
  }

  @Override
  public void archive(Collection<DomainEvent> events) {
    // todo
  }
}
