package de.marcelsauer.order_overdue.application;

import static de.marcelsauer.order_overdue.DomainModule.PREFIX;

import de.marcelsauer.order_overdue.domain.Order;
import de.marcelsauer.order_overdue.domain.OrderRepository;
import de.marcelsauer.use_case.PrefixedLog;
import de.marcelsauer.usecase.Command;
import de.marcelsauer.usecase.UseCase;
import de.marcelsauer.usecase.UseCaseResult;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(PREFIX + "ProcessOrderUserCase")
public class ProcessOrderUserCase
    implements UseCase<ProcessOrderUserCase.ProcessOrderUserCaseCommand> {

  private final PrefixedLog log = PrefixedLog.of(PREFIX);
  private final OrderRepository orderRepository;

  public ProcessOrderUserCase(
      @Qualifier(PREFIX + "OrderRepositoryPostgres") OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
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
