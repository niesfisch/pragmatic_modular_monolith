package de.marcelsauer.order_intake.application;

import static de.marcelsauer.order_intake.DomainModule.PREFIX;

import de.marcelsauer.event.internal.order_intake.OrderCreatedEventV1;
import de.marcelsauer.event_store.EventStoreInternal;
import de.marcelsauer.order_intake.domain.Order;
import de.marcelsauer.order_intake.domain.OrderRepository;
import de.marcelsauer.use_case.PrefixedLog;
import de.marcelsauer.usecase.Command;
import de.marcelsauer.usecase.UseCase;
import de.marcelsauer.usecase.UseCaseResult;
import de.marcelsauer.valueobject.OrderId;
import java.util.*;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(PREFIX + "ProcessOrderUserCase")
public class ProcessOrderUserCase
    implements UseCase<ProcessOrderUserCase.ProcessOrderUserCaseCommand> {

  private final PrefixedLog log = PrefixedLog.of(PREFIX);
  private final OrderRepository orderRepository;
  private final EventStoreInternal eventStoreInternal;
  private final Faker faker = new Faker();

  public ProcessOrderUserCase(
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
    if (isDuplicate(command)) {
      log.info("Order already exists, skipping: %s".formatted(command.externalId));
      return UseCaseResult.success(command);
    }

    // store new order
    Order order =
        new Order(command.firstname, command.lastname, command.articleNr, command.externalId);
    orderRepository.persist(order);

    // store event to propagate the order creation later on
    eventStoreInternal.store(
        new OrderCreatedEventV1(
            order.getId(), order.getFirstname(), order.getLastname(), order.getArticleNr()));

    Map<Command, UseCaseResult.Failure> failures = new HashMap<>();
    Collection<Command> successes = new ArrayList<>();

    // simulate some failures
    if (faker.bool().bool()) {
      failures.put(
          command,
          new UseCaseResult.Failure(
              command, new UseCaseResult.FailureReason(faker.chuckNorris().fact())));
    } else {
      successes.add(command);
    }

    return UseCaseResult.of(successes, failures);
  }

  private boolean isDuplicate(ProcessOrderUserCaseCommand command) {
    return orderRepository
        .findById(
            new OrderId(
                // we derive our internal id from the external id for replayability
                UUID.fromString(command.externalId)))
        .isPresent();
  }

  // describes the input for the use case, it can be called from different adapters like REST,
  // Kafka, etc.
  // so this should drive the design of the use case
  @RequiredArgsConstructor
  public static class ProcessOrderUserCaseCommand implements Command {
    private final String firstname;
    private final String lastname;
    private final int articleNr;
    private final String externalId;
  }
}
