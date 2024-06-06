package de.marcelsauer.event.internal;

public class VersionCleaner {
  // e.g. de.marcelsauer.event.OrderOverdueEventV2.v2 -> de.marcelsauer.event.OrderOverdueEvent.v2
  public static String clean(String in) {
    return in.replaceAll("V\\d+", "");
  }
}
