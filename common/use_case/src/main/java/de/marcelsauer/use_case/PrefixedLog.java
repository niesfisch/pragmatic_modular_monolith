package de.marcelsauer.use_case;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrefixedLog {
  private final String prefix;

  public static PrefixedLog of(String prefix) {
    return new PrefixedLog(prefix);
  }

  private PrefixedLog(String prefix) {
    this.prefix = prefix;
  }

  public void info(String message) {
    log.info("[{}] {}", prefix, message);
  }

  public void error(String message, Exception e) {
    log.error("[{}] {}", prefix, message, e);
  }

  public void error(String message) {
    log.error("[{}] {}", prefix, message);
  }
}
