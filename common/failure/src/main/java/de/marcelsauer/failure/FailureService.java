package de.marcelsauer.failure;

import java.util.Collection;
import java.util.List;

public interface FailureService {

  default void handle(Failure failure) {
    handle(List.of(failure));
  }

  void handle(Collection<Failure> failure);
}
