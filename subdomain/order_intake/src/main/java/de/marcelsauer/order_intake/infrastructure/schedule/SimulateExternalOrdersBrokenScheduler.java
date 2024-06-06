package de.marcelsauer.order_intake.infrastructure.schedule;

import de.marcelsauer.Topics;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimulateExternalOrdersBrokenScheduler {

  private final Faker faker = new Faker();

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  // simulate some external order events coming from another team
  @SneakyThrows
  @Scheduled(fixedRate = 4000)
  void simulate() {
    KafkaTemplate<String, String> invalidKafkaTemplate = invalidKafkaTemplate();
    for (int i = 0; i < faker.random().nextInt(1, 4); i++) {
      // simulate some payload that is no cloud event
      // should be picked up by FailureServiceAwareErrorHandler.java
      invalidKafkaTemplate
          .send(Topics.External.Incoming.TOPIC_EXTERNAL_TEAM_NEW_ORDER, "no cloud event")
          .get();
    }
  }

  private ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  private KafkaTemplate<String, String> invalidKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
