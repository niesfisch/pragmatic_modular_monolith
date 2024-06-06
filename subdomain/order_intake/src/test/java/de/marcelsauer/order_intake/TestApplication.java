package de.marcelsauer.order_intake;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "de.marcelsauer")
public class TestApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(TestApplication.class).run(args);
  }
}
