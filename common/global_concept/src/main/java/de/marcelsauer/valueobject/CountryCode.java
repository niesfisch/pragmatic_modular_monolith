package de.marcelsauer.valueobject;

public final class CountryCode extends SimpleValueObject<String> {

  public CountryCode(String countryCode) {
    super(countryCode, "[A-Z]{2,3}");
  }
}
