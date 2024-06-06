package de.marcelsauer.order_shipment.application;

import static de.marcelsauer.order_shipment.DomainModule.PREFIX;

import de.marcelsauer.event.internal.order_shipment.OrderShippedV1;
import de.marcelsauer.event_store.EventStoreInternal;
import de.marcelsauer.order_shipment.domain.OrderRepository;
import de.marcelsauer.use_case.PrefixedLog;
import de.marcelsauer.usecase.Command;
import de.marcelsauer.usecase.UseCase;
import de.marcelsauer.usecase.UseCaseResult;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(PREFIX + "ProcessShipmentUseCase")
public class ProcessShipmentUseCase
    implements UseCase<ProcessShipmentUseCase.ProcessShipmentUseCaseCommand> {

  private final PrefixedLog log = PrefixedLog.of(PREFIX);
  private final OrderRepository orderRepository;
  private final EventStoreInternal eventStoreInternal;
  private final Faker faker = new Faker();

  public ProcessShipmentUseCase(
      @Qualifier(PREFIX + "OrderRepositoryPostgres") OrderRepository orderRepository,
      @Qualifier(PREFIX + "EventStoreInternal") EventStoreInternal eventStoreInternal) {
    this.orderRepository = orderRepository;
    this.eventStoreInternal = eventStoreInternal;
  }

  @Override
  @Transactional
  public UseCaseResult execute(ProcessShipmentUseCaseCommand command) {
    log.info("Processing order shipment: %s".formatted(command));
    // find order ...
    // validation etc....
    eventStoreInternal.store(
        new OrderShippedV1(
            UUID.randomUUID(), // todo
            command.orderId,
            command.trackingCode,
            command.carrier,
            command.shipmentDateTime));
    return UseCaseResult.of(
        new UseCaseResult.Successes(Collections.emptyList()),
        new UseCaseResult.Failed(Collections.emptyMap()));
  }

  @RequiredArgsConstructor
  public static class ProcessShipmentUseCaseCommand implements Command {
    private final UUID orderId;
    private final String trackingCode;
    private final String carrier;
    private final Instant shipmentDateTime;
  }
}
