package de.marcelsauer.order_intake.domain;

import de.marcelsauer.ddd.DomainEntity;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

// this is our internal domain entity that is used to represent an order
// it is different to the actual entity that is stored in the database
@Getter
@EqualsAndHashCode
public class Order implements DomainEntity {
  private final String firstname;
  private final String lastname;
  private final int articleNr;
  private final String externalId;

  private final UUID id;

  public Order(String firstname, String lastname, int articleNr, String externalId) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.articleNr = articleNr;
    this.externalId = externalId;
    this.id = UUID.fromString(externalId);
  }
}
