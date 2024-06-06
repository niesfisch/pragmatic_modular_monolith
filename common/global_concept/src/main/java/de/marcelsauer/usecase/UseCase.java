package de.marcelsauer.usecase;

public interface UseCase<T extends Command> {
  UseCaseResult execute(T command);
}
