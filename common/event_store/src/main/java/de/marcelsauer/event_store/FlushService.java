package de.marcelsauer.event_store;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.external.outgoing.OrderForOtherTeamInfoEventV1;
import de.marcelsauer.event.internal.DomainEvent;
import de.marcelsauer.event.internal.order_intake.OrderCreatedEventV1;
import de.marcelsauer.event.internal.order_overdue.OrderOverdueEventV1;
import de.marcelsauer.event.internal.order_overdue.OrderOverdueEventV2;
import de.marcelsauer.event.internal.order_shipment.OrderShippedV1;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class FlushService {

  private static final Map<Class<? extends DomainEvent>, String> topicByType =
      Map.of(
          OrderCreatedEventV1.class,
          Topics.Internal.TOPIC_ORDER_CREATED,
          OrderOverdueEventV1.class,
          Topics.Internal.TOPIC_ORDER_OVERDUE,
          OrderOverdueEventV2.class,
          Topics.Internal.TOPIC_ORDER_OVERDUE,
          OrderForOtherTeamInfoEventV1.class,
          Topics.External.Outgoing.TOPIC_ORDER_TO_EXTERNAL_TEAM_ONE,
          OrderShippedV1.class,
          Topics.Internal.TOPIC_ORDER_SHIPPED);

  private final ApplicationContext appCtx;
  private final KafkaTemplate<String, CloudEvent> kafkaTemplate;
  private final ObjectMapper mapper;
  private AtomicLong flushCounter = new AtomicLong();

  @PostConstruct
  private void checkDomainEventsHaveTopics() {
    // load all domain events via reflection on classpath and check
    // if they have a topic assigned
    Reflections reflections = new Reflections("de.marcelsauer");
    Set<Class<? extends DomainEvent>> classes = reflections.getSubTypesOf(DomainEvent.class);
    classes.forEach(
        clazz -> {
          if (!topicByType.containsKey(clazz)) {
            throw new IllegalStateException("no topic assigned for " + clazz.getSimpleName());
          }
        });
  }

  @SneakyThrows
  void flush() {
    Map<String, EventStoreInternal> beansOfType = appCtx.getBeansOfType(EventStoreInternal.class);
    beansOfType.values().forEach(this::doFlush);
  }

  @SneakyThrows
  private void doFlush(EventStoreInternal eventStore) {
    List<DomainEvent> events = eventStore.findAll(1000);
    if (events.isEmpty()) {
      return;
    }
    for (DomainEvent domainEvent : events) {
      String topic = topicByType.get(domainEvent.getClass());
      CloudEvent ce =
          CloudEventBuilder.v1()
              .withId(domainEvent.getId().toString())
              .withSource(URI.create("https://www.marcel-sauer.de"))
              .withType(domainEvent.fqName())
              .withData(mapper.writeValueAsBytes(domainEvent))
              .withDataContentType("application/json")
              .build();
      kafkaTemplate.send(topic, ce);
    }
    eventStore.archive(events);
    flushCounter.addAndGet(events.size());
  }

  @Scheduled(fixedRate = 5000)
  public void printStats() {
    log.info("< events flushed since restart: {}", flushCounter.get());
  }
}
