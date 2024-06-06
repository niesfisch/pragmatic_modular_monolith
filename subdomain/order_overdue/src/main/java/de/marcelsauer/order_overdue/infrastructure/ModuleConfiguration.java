package de.marcelsauer.order_overdue.infrastructure;

import static de.marcelsauer.order_overdue.DomainModule.PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.encryption.DummyEncryptionService;
import de.marcelsauer.encryption.EncryptionService;
import de.marcelsauer.event_store.EventStoreIncoming;
import de.marcelsauer.event_store.EventStoreInternal;
import de.marcelsauer.event_store.postgres.PostgresEventStoreIncoming;
import de.marcelsauer.event_store.postgres.PostgresEventStoreInternal;
import de.marcelsauer.failure.FailureService;
import de.marcelsauer.failure.postgres.PostgresFailureService;
import de.marcelsauer.use_case.FailureServiceAwareErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;

@Configuration(PREFIX + "ModuleConfiguration")
public class ModuleConfiguration {

  @Bean(name = PREFIX + "FailureService")
  public FailureService failureService(ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
    return new PostgresFailureService(
        PREFIX.replaceAll("-", "_") + "__failure",
        new DummyEncryptionService(),
        objectMapper,
        jdbcTemplate);
  }

  @Bean(name = PREFIX + "EventStoreIncoming")
  public EventStoreIncoming eventStoreIncoming(
      ObjectMapper mapper, JdbcTemplate jdbcTemplate, EncryptionService encryptionService) {
    return new PostgresEventStoreIncoming(
        mapper, jdbcTemplate, PREFIX.replaceAll("-", "_") + "__event_incoming", encryptionService);
  }

  @Bean(name = PREFIX + "EventStoreInternal")
  public EventStoreInternal eventStoreInternal(
      ObjectMapper mapper, JdbcTemplate jdbcTemplate, EncryptionService encryptionService) {
    return new PostgresEventStoreInternal(
        mapper, jdbcTemplate, PREFIX.replaceAll("-", "_") + "__event_internal", encryptionService);
  }

  @Bean(name = PREFIX + "CommonErrorHandler")
  public CommonErrorHandler kafkaErrorHandler(
      @Qualifier(PREFIX + "FailureService") FailureService failureService) {
    return new FailureServiceAwareErrorHandler(failureService);
  }

  @Bean(name = PREFIX + "ConcurrentKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
      ConsumerFactory<String, Object> consumerFactory,
      @Qualifier(PREFIX + "CommonErrorHandler") CommonErrorHandler commonErrorHandler) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(commonErrorHandler);
    return factory;
  }
}
