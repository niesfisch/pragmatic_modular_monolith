package de.marcelsauer.valueobject;

import java.util.UUID;

public class OrderId extends SimpleValueObject<UUID> {

  public OrderId(UUID value) {
    super(value);
  }
}
