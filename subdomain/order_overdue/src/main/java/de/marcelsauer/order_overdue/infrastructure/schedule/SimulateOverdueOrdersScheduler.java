package de.marcelsauer.order_overdue.infrastructure.schedule;

import static de.marcelsauer.order_overdue.DomainModule.PREFIX;

import de.marcelsauer.event.internal.order_overdue.OrderOverdueEventV1;
import de.marcelsauer.event.internal.order_overdue.OrderOverdueEventV2;
import de.marcelsauer.event_store.EventStoreInternal;
import java.time.Instant;
import java.util.UUID;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SimulateOverdueOrdersScheduler {

  private final EventStoreInternal eventStoreInternal;
  private final Faker faker = new Faker();

  public SimulateOverdueOrdersScheduler(
      @Qualifier(PREFIX + "EventStoreInternal") EventStoreInternal eventStoreInternal) {
    this.eventStoreInternal = eventStoreInternal;
  }

  @Scheduled(fixedRate = 5000)
  public void findOverdueOrders() {
    // this would normally happen somewhere in a use case
    // do some work and simulate some events with different versions
    if (faker.bool().bool()) {
      eventStoreInternal.store(
          new OrderOverdueEventV1(UUID.randomUUID(), Instant.now().minusSeconds(40)));
    } else {
      eventStoreInternal.store(
          new OrderOverdueEventV2(
              UUID.randomUUID(),
              Instant.now().minusSeconds(40),
              faker.number().numberBetween(1, 100)));
    }
  }
}
