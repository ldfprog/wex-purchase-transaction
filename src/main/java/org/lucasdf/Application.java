package org.lucasdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.lucasdf.repositories")
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
