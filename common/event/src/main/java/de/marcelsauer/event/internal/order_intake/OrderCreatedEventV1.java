package de.marcelsauer.event.internal.order_intake;

import de.marcelsauer.event.internal.DomainEvent;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderCreatedEventV1 extends DomainEvent {

  private String firstname;
  private String lastname;
  private int articleNr;

  @Override
  public int getVersion() {
    return 1;
  }

  // for json
  public OrderCreatedEventV1() {
    super();
  }

  public OrderCreatedEventV1(UUID agggregateId, String firstname, String lastname, int articleNr) {
    super(agggregateId);
    this.firstname = firstname;
    this.lastname = lastname;
    this.articleNr = articleNr;
  }
}
