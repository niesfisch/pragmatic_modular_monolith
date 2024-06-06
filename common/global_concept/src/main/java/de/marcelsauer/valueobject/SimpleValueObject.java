package de.marcelsauer.valueobject;

import java.util.Objects;

public abstract class SimpleValueObject<T> {

  private final T value;

  public SimpleValueObject(T value) {
    // ....
    this.value = value;
  }

  public SimpleValueObject(T value, String regex) {
    // validate mit regex
    this.value = value;
  }

  public T asBaseValue() {
    return this.value;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimpleValueObject<?> that = (SimpleValueObject<?>) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "SimpleValueObject{" + "value=" + value + '}';
  }
}
