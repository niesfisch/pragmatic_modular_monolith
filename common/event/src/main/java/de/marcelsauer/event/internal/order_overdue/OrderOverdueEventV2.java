package de.marcelsauer.event.internal.order_overdue;

import de.marcelsauer.event.internal.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderOverdueEventV2 extends DomainEvent {

  private Instant overdueSince;
  private int someRandomField;

  // for json
  public OrderOverdueEventV2() {}

  @Override
  public int getVersion() {
    return 2;
  }

  public OrderOverdueEventV2(UUID agggregateId, Instant overdueSince, int someRandomField) {
    super(agggregateId);
    this.overdueSince = overdueSince;
    this.someRandomField = someRandomField;
  }
}
