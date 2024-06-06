package de.marcelsauer.order_intake.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.external.incoming.SomeOrderEventFromOtherTeam;
import de.marcelsauer.event_store.EventStoreIncoming;
import de.marcelsauer.failure.FailureService;
import de.marcelsauer.order_intake.DomainModule;
import de.marcelsauer.order_intake.application.ProcessOrderUserCase;
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
public class OrderEventListener extends AbstractConsumerSeekAware {

  private final ProcessOrderUserCase processOrderUserCase;
  private final GenericEventUseCase delegate;

  public OrderEventListener(
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
            () -> DomainModule.PREFIX + "#process order from other team");
  }

  public void reset() {
    seekToBeginning();
    log.info("Seek to beginning");
  }

  @KafkaListener(
      topics = Topics.External.Incoming.TOPIC_EXTERNAL_TEAM_NEW_ORDER,
      groupId = DomainModule.PREFIX)
  @Transactional
  public void onEvent(CloudEvent event) {
    delegate.onEvent(
        event,
        e ->
            processOrderUserCase.execute(
                new ProcessOrderUserCase.ProcessOrderUserCaseCommand(
                    e.firstname(), e.lastname(), e.articleNr(), e.externalId())),
        SomeOrderEventFromOtherTeam.class);
  }
}
