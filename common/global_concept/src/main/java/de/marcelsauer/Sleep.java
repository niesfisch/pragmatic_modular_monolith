package de.marcelsauer;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

@Slf4j
public class Sleep {

  private static final Faker FAKER = new Faker();

  public static void sleepRandomMillis(String msg) {
    sleepMillis(FAKER.random().nextLong(100, 500), msg);
  }

  public static void sleepMillis(long millis, String msg) {
    log.info("{}, sleeping for {}ms", msg, millis);
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // ignore
    }
  }
}
