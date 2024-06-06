package de.marcelsauer.failure;

import static org.assertj.core.api.Assertions.assertThat;

import de.marcelsauer.failure.postgres.PostgresFailureService;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {PostgresFailureServiceIT.Initializer.class})
@Sql({"/test_failure.sql"})
class PostgresFailureServiceIT {

  @Autowired private PostgresFailureService service;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeAll
  static void beforeAll() {
    postgreSQLContainer.start();
  }

  @AfterAll
  static void afterAll() {
    postgreSQLContainer.stop();
  }

  @BeforeEach
  void setUp() {
    jdbcTemplate.execute("TRUNCATE failure");
  }

  @Test
  void handle() {
    // given
    Failure failure =
        Failure.of(
            FailureType.TECHNICAL,
            () -> "dumm usecase",
            "payload",
            "cause",
            () -> "error code 123");

    // when
    service.handle(failure);

    // then
    assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM failure", Integer.class))
        .isEqualTo(1);
  }

  @ClassRule
  public static PostgreSQLContainer<?> postgreSQLContainer =
      (PostgreSQLContainer<?>)
          new PostgreSQLContainer("postgres:14")
              .withDatabaseName("integration-tests-db")
              .withUsername("sa")
              .withPassword("sa")
              .withReuse(true);

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
              "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + postgreSQLContainer.getUsername(),
              "spring.datasource.password=" + postgreSQLContainer.getPassword())
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
