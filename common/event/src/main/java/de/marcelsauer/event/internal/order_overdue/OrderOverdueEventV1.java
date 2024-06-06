package de.marcelsauer.event.internal.order_overdue;

import de.marcelsauer.event.internal.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderOverdueEventV1 extends DomainEvent {

  private UUID orderId;
  private Instant overdueSince;

  // for json
  public OrderOverdueEventV1() {}

  @Override
  public int getVersion() {
    return 1;
  }

  public OrderOverdueEventV1(UUID agggregateId, Instant overdueSince) {
    super(agggregateId);
    this.overdueSince = overdueSince;
  }
}
