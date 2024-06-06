package de.marcelsauer.order_shipment.application;

import static de.marcelsauer.order_shipment.DomainModule.PREFIX;

import de.marcelsauer.event_store.EventStoreInternal;
import de.marcelsauer.order_shipment.domain.Order;
import de.marcelsauer.order_shipment.domain.OrderRepository;
import de.marcelsauer.use_case.PrefixedLog;
import de.marcelsauer.usecase.Command;
import de.marcelsauer.usecase.UseCase;
import de.marcelsauer.usecase.UseCaseResult;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(PREFIX + "ProcessOrderUserCase")
public class ProcessOrderUseCase
    implements UseCase<ProcessOrderUseCase.ProcessOrderUserCaseCommand> {

  private final PrefixedLog log = PrefixedLog.of(PREFIX);
  private final OrderRepository orderRepository;
  private final EventStoreInternal eventStoreInternal;
  private final Faker faker = new Faker();

  public ProcessOrderUseCase(
      @Qualifier(PREFIX + "OrderRepositoryPostgres") OrderRepository orderRepository,
      @Qualifier(PREFIX + "EventStoreInternal") EventStoreInternal eventStoreInternal) {
    this.orderRepository = orderRepository;
    this.eventStoreInternal = eventStoreInternal;
  }

  @Override
  @Transactional
  public UseCaseResult execute(ProcessOrderUserCaseCommand command) {
    log.info("Processing order: %s".formatted(command));
    // validation etc....
    orderRepository.persist(new Order());
    return UseCaseResult.of(
        new UseCaseResult.Successes(Collections.emptyList()),
        new UseCaseResult.Failed(Collections.emptyMap()));
  }

  @RequiredArgsConstructor
  public static class ProcessOrderUserCaseCommand implements Command {
    private final UUID externalOrderId;
  }
}
