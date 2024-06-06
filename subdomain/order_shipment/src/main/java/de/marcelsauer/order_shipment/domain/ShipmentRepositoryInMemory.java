package de.marcelsauer.order_shipment.domain;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ShipmentRepositoryInMemory implements ShipmentRepository {

  @Override
  public boolean isOrderShipped(UUID orderId) {
    return false;
  }
}
