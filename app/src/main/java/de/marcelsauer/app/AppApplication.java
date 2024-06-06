package de.marcelsauer.app;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.FileNotFoundException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties
@EnableScheduling
@SpringBootApplication(scanBasePackages = "de.marcelsauer")
public class AppApplication {

  // todo move this somewhere else
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper =
        new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.findAndRegisterModules();
    return objectMapper;
  }

  public static class CustomGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
      /** access bean annotations or package ... */
      return super.generateBeanName(definition, registry);
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    new SpringApplicationBuilder(AppApplication.class)
        .beanNameGenerator(new CustomGenerator())
        .run(args);
  }
}
