package de.marcelsauer.order_shipment.domain;

import java.util.UUID;

public interface ShipmentRepository {
  boolean isOrderShipped(UUID orderId);
}
