package de.marcelsauer.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.external.incoming.SomeOrderEventFromOtherTeam;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import java.util.UUID;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class SimulateExternalOrderEventLoad {

  @Autowired private KafkaTemplate kafkaTemplate;
  @Autowired private ObjectMapper mapper;
  private final Faker faker = new Faker();

  @SneakyThrows
  @Test
  void simulateSomeExternalOrderEvents() {
    for (int i = 0; i < 100_000; i++) {
      UUID id = UUID.randomUUID();
      SomeOrderEventFromOtherTeam data =
          new SomeOrderEventFromOtherTeam(
              id,
              faker.name().firstName(),
              faker.name().lastName(),
              faker.number().numberBetween(1111111, 99999999),
              faker.internet().uuid());
      CloudEvent ce =
          CloudEventBuilder.v1()
              .withId(id.toString())
              .withSource(URI.create("https://www.marcel-sauer.de"))
              .withType(SomeOrderEventFromOtherTeam.class.getName() + ".v1")
              .withData(mapper.writeValueAsBytes(data))
              .withDataContentType("application/json")
              .build();
      kafkaTemplate.send(Topics.External.Incoming.TOPIC_EXTERNAL_TEAM_NEW_ORDER, ce).get();
    }
  }
}
