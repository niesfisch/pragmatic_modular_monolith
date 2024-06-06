package de.marcelsauer.failure.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.marcelsauer.PgUtil;
import de.marcelsauer.encryption.EncryptionService;
import de.marcelsauer.failure.Failure;
import de.marcelsauer.failure.FailureService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
@Slf4j
public class PostgresFailureService implements FailureService {

  private final String tableName;
  private final EncryptionService encryptionService;
  private final ObjectMapper objectMapper;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public void handle(Collection<Failure> failures) {
    String sql =
        """
INSERT INTO %s (id, type, use_case, payload, cause, error_code, created, trigger_type,trigger, stack_trace)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
"""
            .formatted(tableName);
    List<Object[]> batchArgs =
        failures.stream()
            .map(
                failure ->
                    new Object[] {
                      failure.getId(),
                      failure.getType().name(),
                      failure.getUseCase().name(),
                      encryptionService.encrypt(toJson(failure)),
                      encryptionService.encrypt(failure.getCause()),
                      failure.getErrorCode().code(),
                      PgUtil.toTimestamp(failure.getCreated()),
                      failure.getTriggerType().name(),
                      failure.getTrigger(),
                      encryptionService.encrypt(failure.getStackTrace())
                    })
            .collect(Collectors.toList());

    jdbcTemplate.batchUpdate(sql, batchArgs);
  }

  @SneakyThrows
  private String toJson(Failure failure) {
    return objectMapper.writeValueAsString(failure.getPayload());
  }
}
