package de.marcelsauer.event_store;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/** use this handler to repeat sending in intervals, e.g. when calling from a cloud scheduler */
@Slf4j
@RequiredArgsConstructor
public class ReschedulingQueuedOutboxHandler {

  private final FlushService flushService;

  // only one run per jvm in parallel
  private Semaphore semaphore = new Semaphore(1);

  @SneakyThrows
  public void flush(int repeatCount, int pauseSecondsBetweenRepeat) {
    try {
      if (!semaphore.tryAcquire()) {
        return;
      }
      for (int i = 0; i < repeatCount; i++) {
        flushService.flush();
        TimeUnit.SECONDS.sleep(pauseSecondsBetweenRepeat);
      }
    } finally {
      semaphore.release();
    }
  }
}
