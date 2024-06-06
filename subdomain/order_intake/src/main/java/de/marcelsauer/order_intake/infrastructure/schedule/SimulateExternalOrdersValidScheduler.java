package de.marcelsauer.order_intake.infrastructure.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.external.incoming.SomeOrderEventFromOtherTeam;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimulateExternalOrdersValidScheduler {

  private final KafkaTemplate kafkaTemplate;
  private final Faker faker = new Faker();
  private final ObjectMapper mapper;

  // simulate some external order events coming from another team
  @SneakyThrows
  @Scheduled(fixedRate = 5000)
  void simulate() {
    for (int i = 0; i < faker.random().nextInt(1, 4); i++) {
      send();
    }
  }

  private void send() throws JsonProcessingException, InterruptedException, ExecutionException {
    UUID id = UUID.randomUUID();
    // valid cloud event + valid json data payload
    Object data = getValid(id);
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

  private SomeOrderEventFromOtherTeam getValid(UUID id) {
    return new SomeOrderEventFromOtherTeam(
        id,
        faker.name().firstName(),
        faker.name().lastName(),
        faker.number().numberBetween(1111111, 99999999),
        faker.internet().uuid());
  }
}
