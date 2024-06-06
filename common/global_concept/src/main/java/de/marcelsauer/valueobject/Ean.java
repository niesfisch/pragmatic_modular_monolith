package de.marcelsauer.valueobject;

public class Ean extends SimpleValueObject<String> {

  public Ean(String value) {
    super(value, "\\w{8,10}");
  }
}
