package de.marcelsauer.failure;

import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Failure {

  private final UUID id;
  private final FailureType type;
  private final UseCase useCase;
  private final String payload;
  private final String cause;
  private final ErrorCode errorCode;
  private final Instant created;
  private final TriggerType triggerType;
  private final String trigger;
  private final String stackTrace;

  public static Failure of(
      FailureType type, UseCase useCase, String payload, String cause, ErrorCode errorCode) {
    return new Failure(
        type,
        useCase,
        payload,
        cause,
        errorCode,
        TriggerType.UNKNOWN, // TODO
        "n.a.",
        "n.a.");
  }

  public static Failure of(
      FailureType type,
      UseCase useCase,
      String payload,
      String cause,
      ErrorCode errorCode,
      TriggerType triggerType,
      String trigger) {
    return new Failure(
        type,
        useCase,
        payload,
        cause,
        errorCode,
        triggerType, // TODO
        trigger,
        "n.a.");
  }

  private Failure(
      FailureType type,
      UseCase useCase,
      String payload,
      String cause,
      ErrorCode errorCode,
      TriggerType triggerType,
      String trigger,
      String stackTrace) {
    this.id = UUID.randomUUID();
    this.type = type;
    this.useCase = useCase;
    this.payload = payload;
    this.cause = cause;
    this.errorCode = errorCode;
    this.created = Instant.now();
    this.triggerType = triggerType;
    this.trigger = trigger;
    this.stackTrace = stackTrace;
  }
}
