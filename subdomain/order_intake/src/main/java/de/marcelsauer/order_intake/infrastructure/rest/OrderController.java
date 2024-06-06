package de.marcelsauer.order_intake.infrastructure.rest;

// import de.marcelsauer.order_intake.application.ProcessOrderUserCase;
// import de.marcelsauer.usecase.UseCaseResult;
// import java.util.UUID;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RestController;

import de.marcelsauer.order_intake.application.ProcessOrderUserCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// @RestController
public class OrderController {
  private final ProcessOrderUserCase userCase;

  //  //@PostMapping("/order")
  //  public ResponseEntity<String> createOrder() {
  //    UUID externalOrderId = UUID.randomUUID();
  //    UseCaseResult execute =
  //        userCase.execute(new ProcessOrderUserCase.ProcessOrderUserCaseCommand(externalOrderId));
  //    // map to response etc
  //    return null;
  //  }
}
