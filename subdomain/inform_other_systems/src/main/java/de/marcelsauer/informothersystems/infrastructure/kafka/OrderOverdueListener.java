package de.marcelsauer.informothersystems.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.internal.order_overdue.OrderOverdueEventV1;
import de.marcelsauer.event.internal.order_overdue.OrderOverdueEventV2;
import de.marcelsauer.event_store.EventStoreIncoming;
import de.marcelsauer.failure.FailureService;
import de.marcelsauer.informothersystems.DomainModule;
import de.marcelsauer.informothersystems.application.ProcessOrderOverdueCase;
import de.marcelsauer.informothersystems.application.ProcessOrderUseCase;
import de.marcelsauer.use_case.GenericEventUseCase;
import de.marcelsauer.use_case.PrefixedLog;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component(DomainModule.PREFIX + "OrderEventListener")
public class OrderOverdueListener extends AbstractConsumerSeekAware {

  private final ProcessOrderUseCase processOrderUserCase;
  private final GenericEventUseCase delegate;

  public OrderOverdueListener(
      ProcessOrderUseCase processOrderUserCase,
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
            () -> DomainModule.PREFIX + "#process order overdue");
  }

  @KafkaListener(topics = Topics.Internal.TOPIC_ORDER_OVERDUE, groupId = DomainModule.PREFIX)
  @Transactional
  public void onEvent(CloudEvent event) {

    if ("de.marcelsauer.event.OrderOverdueEvent.v1".equals(event.getType())) {
      delegate.onEvent(
          event,
          e ->
              processOrderUserCase.execute(
                  new ProcessOrderOverdueCase.ProcessOrderOverdueCommand()),
          OrderOverdueEventV1.class);
    } else {
      delegate.onEvent(
          event,
          e ->
              processOrderUserCase.execute(
                  new ProcessOrderOverdueCase.ProcessOrderOverdueCommand()),
          OrderOverdueEventV2.class);
    }
  }

  public void reset() {
    seekToBeginning();
    log.info("Seek to beginning");
  }
}
