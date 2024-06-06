package de.marcelsauer.event;

import static org.assertj.core.api.Assertions.assertThat;

import de.marcelsauer.event.internal.VersionCleaner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VersionCleanerTest {

  @ParameterizedTest
  @CsvSource({
    "de.OrderOverdueEventV1.v1, de.OrderOverdueEvent.v1",
    "de.marcelsauer.OrderCreatedEventV1.v1, de.marcelsauer.OrderCreatedEvent.v1",
    "de.marcelsauer.event.OrderForOtherTeamInfoEventV22.v22,"
        + " de.marcelsauer.event.OrderForOtherTeamInfoEvent.v22"
  })
  void clean(String input, String expected) {
    assertThat(VersionCleaner.clean(input)).isEqualTo(expected);
  }
}
