package de.marcelsauer;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PgUtil {
  public static Timestamp toTimestamp(Instant instant) {
    LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    return Timestamp.valueOf(ldt);
  }
}
