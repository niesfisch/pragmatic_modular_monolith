package de.marcelsauer.use_case;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.Sleep;
import de.marcelsauer.event_store.EventStoreIncoming;
import de.marcelsauer.failure.*;
import de.marcelsauer.usecase.UseCaseResult;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class GenericEventUseCase {

  private final FailureService failureService;
  private final EventStoreIncoming eventStoreIncoming;
  private final PrefixedLog log;
  private final ObjectMapper mapper;
  private final UseCase useCase;

  public interface Callback<T> {
    UseCaseResult onEventInternal(T event);
  }

  @SneakyThrows
  public <T> void onEvent(CloudEvent event, Callback<T> callback, Class<T> targetClass) {
    T incomingEvent;
    try {
      PojoCloudEventData<?> data =
          CloudEventUtils.mapData(event, PojoCloudEventDataMapper.from(mapper, targetClass));
      incomingEvent = (T) data.getValue();
    } catch (Exception e) {
      handleMappingFailure(event, targetClass, e);
      return;
    }

    if (eventStoreIncoming.exists(event.getId())) {
      log.info(
          "Event (%s|%s) already processed, skipping".formatted(event.getId(), event.getType()));
      return;
    }

    Sleep.sleepRandomMillis("simulating some process time ...");

    UseCaseResult result;
    try {
      result = callback.onEventInternal(incomingEvent);
    } catch (RuntimeException e) {
      // something unexpected happend during processing
      // we just signal that so that reprocessing can happen
      // todo this could lead to an endless loop
      // todo think about backoff, retries, etc.
      log.error("error processing event", e);
      throw new RuntimeException(e);
    }

    // do something with the successes
    UseCaseResult.Successes successes = result.getSuccessfull();

    UseCaseResult.Failed failed = result.getFailed();
    Collection<Failure> failures = new ArrayList<>();
    failed
        .failures()
        .forEach(
            (command, failure) -> {
              failures.add(
                  Failure.of(
                      FailureType.BUSINESS,
                      useCase,
                      toJson(incomingEvent),
                      failure.reason().reason(),
                      () -> ""));
            });

    failureService.handle(failures);

    // store incoming orderEventFromExternalTeam
    eventStoreIncoming.store(EventStoreIncoming.IncomingEvent.of(event.getId(), event.toString()));
  }

  private <T> void handleMappingFailure(CloudEvent event, Class<T> targetClass, Exception e)
      throws JsonProcessingException {
    log.error(
        "could not deserialize incoming cloud event payload to target type '%s'"
            .formatted(targetClass.getName()));
    failureService.handle(
        Failure.of(
            FailureType.TECHNICAL,
            useCase,
            mapper.writeValueAsString(new Payload(new String(event.getData().toBytes()), event)),
            mapper.writeValueAsString(
                new Cause(
                    "error mapping incoming event", e.getMessage(), e.getCause().getMessage())),
            () -> "123", // todo
            TriggerType.MESSAGE,
            event.getId()));
  }

  @SneakyThrows
  private <T> String toJson(T incomingEvent) {
    return mapper.writeValueAsString(incomingEvent);
  }

  private record Payload(String payload, CloudEvent event) {}

  private record Cause(String headline, String cause1, String cause2) {}
}
