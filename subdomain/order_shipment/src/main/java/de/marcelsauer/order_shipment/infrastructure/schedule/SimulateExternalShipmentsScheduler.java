package de.marcelsauer.order_shipment.infrastructure.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.external.incoming.OrderShippedBySupplier;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimulateExternalShipmentsScheduler {

  private final KafkaTemplate kafkaTemplate;
  private final Faker faker = new Faker();
  private final ObjectMapper mapper;

  // simulate some external order shipment events
  @Scheduled(fixedRate = 6000)
  void simulate() {
    for (int i = 0; i < faker.random().nextInt(1, 3); i++) {
      send();
    }
  }

  @SneakyThrows
  private void send() {
    UUID id = UUID.randomUUID();

    Object data;
    if (faker.bool().bool()) {
      data = getValid(id);
    } else {
      data = getInvalid(id);
    }

    CloudEvent ce =
        CloudEventBuilder.v1()
            .withId(id.toString())
            .withSource(URI.create("https://www.marcel-sauer.de"))
            .withType(OrderShippedBySupplier.class.getName() + ".v1")
            .withData(mapper.writeValueAsBytes(data))
            .withDataContentType("application/json")
            .build();
    kafkaTemplate.send(Topics.External.Incoming.TOPIC_ORDER_SHIPPED_BY_SUPPLIER, ce).get();
  }

  private String getInvalid(UUID id) {
    return "some broken json %s".formatted(id);
  }

  private OrderShippedBySupplier getValid(UUID id) {
    return new OrderShippedBySupplier(
        id,
        UUID.randomUUID(),
        faker.lorem().word(),
        String.valueOf(faker.random().nextInt(1000000, 9999999)),
        Instant.now().minus(faker.random().nextInt(1, 2), ChronoUnit.DAYS));
  }
}
