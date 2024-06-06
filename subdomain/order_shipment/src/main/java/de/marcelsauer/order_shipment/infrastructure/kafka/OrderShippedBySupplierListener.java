package de.marcelsauer.order_shipment.infrastructure.kafka;

import static de.marcelsauer.order_shipment.DomainModule.PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Topics;
import de.marcelsauer.event.external.incoming.OrderShippedBySupplier;
import de.marcelsauer.event_store.EventStoreIncoming;
import de.marcelsauer.failure.FailureService;
import de.marcelsauer.order_shipment.DomainModule;
import de.marcelsauer.order_shipment.application.ProcessShipmentUseCase;
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
@Component(PREFIX + "OrderShippedBySupplierListener")
public class OrderShippedBySupplierListener extends AbstractConsumerSeekAware {

  private final ProcessShipmentUseCase useCase;
  private final GenericEventUseCase delegate;

  public OrderShippedBySupplierListener(
      ProcessShipmentUseCase useCase,
      @Qualifier(DomainModule.PREFIX + "FailureService") FailureService failureService,
      @Qualifier(DomainModule.PREFIX + "EventStoreIncoming") EventStoreIncoming eventStoreIncoming,
      ObjectMapper mapper) {
    this.useCase = useCase;
    this.delegate =
        new GenericEventUseCase(
            failureService,
            eventStoreIncoming,
            PrefixedLog.of(DomainModule.PREFIX),
            mapper,
            () -> PREFIX + "#process order shipped by supplier");
  }

  @KafkaListener(
      topics = Topics.External.Incoming.TOPIC_ORDER_SHIPPED_BY_SUPPLIER,
      groupId = PREFIX)
  @Transactional
  public void onEvent(CloudEvent event) {
    delegate.onEvent(
        event,
        e ->
            useCase.execute(
                new ProcessShipmentUseCase.ProcessShipmentUseCaseCommand(
                    e.orderId(), e.trackingCode(), e.carrier(), e.shipmentDateTime())),
        OrderShippedBySupplier.class);
  }

  public void reset() {
    seekToBeginning();
    log.info("Seek to beginning");
  }
}
