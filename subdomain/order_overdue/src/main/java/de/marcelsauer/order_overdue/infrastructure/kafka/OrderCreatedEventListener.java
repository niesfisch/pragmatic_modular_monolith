package de.marcelsauer.order_overdue.infrastructure.kafka;

import static de.marcelsauer.order_overdue.DomainModule.PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.internal.order_intake.OrderCreatedEventV1;
import de.marcelsauer.event_store.EventStoreIncoming;
import de.marcelsauer.failure.FailureService;
import de.marcelsauer.order_overdue.DomainModule;
import de.marcelsauer.order_overdue.application.ProcessOrderUserCase;
import de.marcelsauer.use_case.GenericEventUseCase;
import de.marcelsauer.use_case.PrefixedLog;
import io.cloudevents.CloudEvent;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component(PREFIX + "OrderEventListener")
public class OrderCreatedEventListener extends AbstractConsumerSeekAware {

  private final ProcessOrderUserCase processOrderUserCase;
  private final GenericEventUseCase delegate;

  public OrderCreatedEventListener(
      ProcessOrderUserCase processOrderUserCase,
      @Qualifier(DomainModule.PREFIX + "FailureService") FailureService failureService,
      @Qualifier(DomainModule.PREFIX + "EventStoreIncoming") EventStoreIncoming eventStoreIncoming,
      ObjectMapper mapper) {
    this.processOrderUserCase = processOrderUserCase;
    this.delegate =
        new GenericEventUseCase(
            failureService,
            eventStoreIncoming,
            PrefixedLog.of(DomainModule.PREFIX),
            mapper,
            () -> PREFIX + "#process order created");
  }

  @KafkaListener(topics = Topics.Internal.TOPIC_ORDER_CREATED, groupId = PREFIX)
  @Transactional
  public void onEvent(CloudEvent event) {
    delegate.onEvent(
        event,
        e ->
            processOrderUserCase.execute(
                new ProcessOrderUserCase.ProcessOrderUserCaseCommand(UUID.randomUUID())),
        OrderCreatedEventV1.class);
  }

  public void reset() {
    seekToBeginning();
    log.info("Seek to beginning");
  }
}
