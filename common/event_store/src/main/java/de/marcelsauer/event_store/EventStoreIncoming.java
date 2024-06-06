package de.marcelsauer.event_store;

import java.util.UUID;

public interface EventStoreIncoming {
  void store(IncomingEvent incomingEvent);

  boolean exists(String eventId);

  boolean exists(UUID eventId);

  class IncomingEvent {
    private final String id; // we can't assume that the id is a UUID
    private final String payload;

    private IncomingEvent(String id, String payload) {
      this.id = id;
      this.payload = payload;
    }

    public static IncomingEvent of(String id, String payload) {
      return new IncomingEvent(id, payload);
    }

    public String id() {
      return id;
    }
  }
}
