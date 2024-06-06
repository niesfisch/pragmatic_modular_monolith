package de.marcelsauer.order_overdue.infrastructure.rest;

import de.marcelsauer.order_overdue.domain.OverdueRepository;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.marcelsauer.order_overdue.DomainModule.PREFIX;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/" + PREFIX + "/api")
public class OverdueOrdersController {

  private final OverdueRepository overdueRepository;

  @GetMapping("/overdueOrders")
  public Collection<OverdueOrder> getOverdueOrders() {
    log.info("simulating some random overdue orders");
    Faker faker = new Faker();
    return overdueRepository.findAllOverdueOrders().stream()
        .map(
            it -> {
              String firstname = faker.name().firstName();
              String lastname = faker.name().lastName();
              return new OverdueOrder(UUID.randomUUID(), firstname, lastname);
            })
        .toList();
  }
}
