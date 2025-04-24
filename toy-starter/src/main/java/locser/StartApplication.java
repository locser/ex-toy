package locser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = { "locser.toy.domain.model.entity" })
@EnableJpaRepositories(basePackages = { "locser.persistence.mapper" })
@ComponentScan(basePackages = { "locser", "locser.controller", "locser.controller.config",
    "locser.controller.exception" })
public class StartApplication {

  public static void main(String[] args) {
    System.out.println("Hello world!");
    SpringApplication.run(StartApplication.class, args);
  }
}