package de.marcelsauer.app;

import de.marcelsauer.informothersystems.infrastructure.kafka.OrderCreatedListener;
import de.marcelsauer.informothersystems.infrastructure.kafka.OrderOverdueListener;
import de.marcelsauer.order_intake.infrastructure.kafka.OrderEventListener;
import de.marcelsauer.order_overdue.infrastructure.kafka.OrderCreatedEventListener;
import de.marcelsauer.order_shipment.infrastructure.kafka.OrderShippedBySupplierListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/api")
public class ResetConsumerOffsetsController {

  private final OrderEventListener listener1;
  private final OrderCreatedListener listener2;
  private final OrderOverdueListener listener3;
  private final OrderCreatedEventListener listener4;
  private final OrderShippedBySupplierListener listener5;

  // todo
  @GetMapping("/reset-consumer-offsets")
  public void reset() {
    listener1.reset();
    listener2.reset();
    listener3.reset();
    listener4.reset();
    listener5.reset();
  }
}
