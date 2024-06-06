package de.marcelsauer.event_store;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlushEventsScheduler {

  private final FlushService flushService;

  @Scheduled(fixedRate = 200)
  public void flush() {
    // this would normally be called by a cloud scheduler via rest endpoint
    new ReschedulingQueuedOutboxHandler(flushService).flush(5, 1);
  }
}
