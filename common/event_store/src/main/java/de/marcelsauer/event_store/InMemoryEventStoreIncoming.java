package de.marcelsauer.event_store;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InMemoryEventStoreIncoming implements EventStoreIncoming {
  private static final Map<String, IncomingEvent> events = new HashMap<>();

  private final String tableName;

  @Override
  public void store(IncomingEvent incomingEvent) {
    log.info("storing event: {}", incomingEvent);
    events.put(incomingEvent.id(), incomingEvent);
  }

  @Override
  public boolean exists(String eventId) {
    log.info("checking if event exists {}", eventId);

    // return random boolean
    return Math.random() < 0.5;
    // return events.containsKey(eventId);
  }

  @Override
  public boolean exists(UUID eventId) {
    return exists(eventId.toString());
  }
}
