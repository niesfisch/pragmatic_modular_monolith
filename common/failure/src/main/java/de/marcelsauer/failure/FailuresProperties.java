package de.marcelsauer.failure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pex.failures")
public class FailuresProperties {

  private String failureTable;

  public String getFailureTable() {
    return failureTable;
  }

  public void setFailureTable(String failureTable) {
    this.failureTable = failureTable;
  }
}
