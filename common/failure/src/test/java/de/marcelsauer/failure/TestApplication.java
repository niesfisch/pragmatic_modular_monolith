package de.marcelsauer.failure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.marcelsauer.encryption.DummyEncryptionService;
import de.marcelsauer.failure.postgres.PostgresFailureService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication(scanBasePackages = "de.marcelsauer")
public class TestApplication {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    //        JavaTimeModule module = new JavaTimeModule();
    //        module.addSerializer(LOCAL_DATETIME_SERIALIZER);
    ObjectMapper objectMapper =
        new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // .registerModule(module);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return objectMapper;
  }

  @Bean
  public PostgresFailureService postgresFailureService(
      ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
    return new PostgresFailureService(
        "failure", new DummyEncryptionService(), objectMapper, jdbcTemplate);
  }
}
