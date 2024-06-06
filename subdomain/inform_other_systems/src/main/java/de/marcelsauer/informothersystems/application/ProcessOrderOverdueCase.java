package de.marcelsauer.informothersystems.application;

import static de.marcelsauer.informothersystems.DomainModule.PREFIX;

import de.marcelsauer.event.external.outgoing.OrderForOtherTeamInfoEventV1;
import de.marcelsauer.event_store.EventStoreInternal;
import de.marcelsauer.informothersystems.DomainModule;
import de.marcelsauer.informothersystems.domain.Order;
import de.marcelsauer.informothersystems.domain.OrderRepository;
import de.marcelsauer.use_case.PrefixedLog;
import de.marcelsauer.usecase.Command;
import de.marcelsauer.usecase.UseCase;
import de.marcelsauer.usecase.UseCaseResult;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(PREFIX + "ProcessOrderOverdueCase")
public class ProcessOrderOverdueCase implements UseCase {

  private final OrderRepository orderRepository;
  private final KafkaTemplate kafkaTemplate;
  private final EventStoreInternal eventStoreInternal;
  private final PrefixedLog log = PrefixedLog.of(DomainModule.PREFIX);

  public ProcessOrderOverdueCase(
      @Qualifier(PREFIX + "OrderRepositoryPostgres") OrderRepository orderRepository,
      KafkaTemplate kafkaTemplate,
      @Qualifier(PREFIX + "EventStoreInternal") EventStoreInternal eventStoreInternal) {
    this.orderRepository = orderRepository;
    this.kafkaTemplate = kafkaTemplate;
    this.eventStoreInternal = eventStoreInternal;
  }

  @Override
  @Transactional
  public UseCaseResult execute(Command command) {
    log.info("Processing overdue order: %s".formatted(command));
    // validation etc....
    orderRepository.persist(new Order());
    eventStoreInternal.store(new OrderForOtherTeamInfoEventV1(UUID.randomUUID(), "some dummy"));
    return UseCaseResult.of(
        new UseCaseResult.Successes(java.util.Collections.emptyList()),
        new UseCaseResult.Failed(java.util.Collections.emptyMap()));
  }

  @RequiredArgsConstructor
  public static class ProcessOrderOverdueCommand implements Command {}
}
