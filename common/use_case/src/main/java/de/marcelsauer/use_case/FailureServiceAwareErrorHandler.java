package de.marcelsauer.use_case;

import static org.springframework.kafka.support.serializer.SerializationUtils.KEY_DESERIALIZER_EXCEPTION_HEADER;
import static org.springframework.kafka.support.serializer.SerializationUtils.VALUE_DESERIALIZER_EXCEPTION_HEADER;

import de.marcelsauer.failure.Failure;
import de.marcelsauer.failure.FailureService;
import de.marcelsauer.failure.FailureType;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;

@RequiredArgsConstructor
@Slf4j
public class FailureServiceAwareErrorHandler implements CommonErrorHandler {

  private final FailureService failureService;

  /**
   * get's called when an exception during deserialization of cloud event occurs (e.g. headers
   * missing etc.) the actual deserialization of the nested object (data) is not done here but in
   * the use cases
   *
   * @see org.springframework.kafka.support.serializer.SerializationUtils.deserializationException
   * @see
   *     org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.deserialize(java.lang.String,
   *     org.apache.kafka.common.header.Headers, byte[])
   *     <p>configured via application.properties:
   *     spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
   */
  @Override
  public boolean handleOne(
      Exception thrownException,
      ConsumerRecord<?, ?> record,
      Consumer<?, ?> consumer,
      MessageListenerContainer container) {

    if (record.headers() == null) {
      return CommonErrorHandler.super.handleOne(thrownException, record, consumer, container);
    }

    record
        .headers()
        .forEach(
            header -> {
              if (header.key().equals(VALUE_DESERIALIZER_EXCEPTION_HEADER)) {
                try {
                  ObjectInputStream objectInputStream =
                      new ObjectInputStream(new ByteArrayInputStream(header.value()));
                  DeserializationException o =
                      (DeserializationException) objectInputStream.readObject();
                  String msg1 = o.getMessage();
                  String msg2 = o.getCause().getMessage();
                  String dataBase64 = Base64.getEncoder().encodeToString(o.getData());
                  StringBuilder headers = new StringBuilder();
                  if (o.getHeaders() != null) {
                    o.getHeaders()
                        .forEach(
                            h -> {
                              headers.append("%s:%s###".formatted(h.key(), h.value()));
                            });
                  }

                  failureService.handle(
                      Failure.of(
                          FailureType.TECHNICAL,
                          () ->
                              "deserialization of kafka message of topic '%s'"
                                  .formatted(record.topic()),
                          String.join("||", msg1, msg2, dataBase64, headers.toString()),
                          String.join("||", msg1, msg2),
                          () -> "n.a."));

                } catch (Exception e) {
                  log.error("error handling {}", thrownException.getMessage());
                }
              } else if (header.key().equals(KEY_DESERIALIZER_EXCEPTION_HEADER)) {
                // todo: handle key deserialization exception
                log.error("key deserialization exception todo ...");
              }
            });
    // signal that the record has been handled
    return true;
  }
}
