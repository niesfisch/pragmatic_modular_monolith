package de.marcelsauer.event.internal.order_shipment;

import de.marcelsauer.event.internal.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderShippedV1 extends DomainEvent {

  private UUID orderId;
  private String carrier;
  private String trackingCode;
  private Instant shipmentDateTime;

  // for json
  public OrderShippedV1() {}

  @Override
  public int getVersion() {
    return 1;
  }

  public OrderShippedV1(
      UUID agggregateId,
      UUID orderId,
      String carrier,
      String trackingCode,
      Instant shipmentDateTime) {
    super(agggregateId);
    this.orderId = orderId;
    this.carrier = carrier;
    this.trackingCode = trackingCode;
    this.shipmentDateTime = shipmentDateTime;
  }
}
