package de.marcelsauer.usecase;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UseCaseResult {

  private final Successes successfull;
  private final Failed failed;

  private UseCaseResult(Successes successfull, Failed failed) {
    this.successfull = successfull;
    this.failed = failed;
  }

  public static UseCaseResult success(Command command) {
    return new UseCaseResult(new Successes(List.of(command)), new Failed(Map.of()));
  }

  public static UseCaseResult of(Successes successes, Failed failed) {
    return new UseCaseResult(successes, failed);
  }

  public static UseCaseResult of(Collection<Command> successes, Map<Command, Failure> failures) {
    return new UseCaseResult(new Successes(successes), new Failed(failures));
  }

  public record Successes(Collection<Command> commands) {}

  public record Failed(Map<Command, Failure> failures) {
    public void add(Command command, FailureReason reason) {
      failures.put(command, new Failure(command, reason));
    }
  }

  public record Failure(Command command, FailureReason reason) {}

  public record FailureReason(String reason) {}
}
