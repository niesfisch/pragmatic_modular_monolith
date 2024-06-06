package de.marcelsauer.event.external.outgoing;

import de.marcelsauer.event.internal.DomainEvent;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderForOtherTeamInfoEventV1 extends DomainEvent {

  private String dummyValue;

  // for json
  public OrderForOtherTeamInfoEventV1() {}

  @Override
  public int getVersion() {
    return 1;
  }

  public OrderForOtherTeamInfoEventV1(UUID agggregateId, String dummyValue) {
    super(agggregateId);
    this.dummyValue = dummyValue;
  }
}
