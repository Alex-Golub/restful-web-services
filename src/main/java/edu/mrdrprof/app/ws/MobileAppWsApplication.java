package edu.mrdrprof.app.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@PropertySource("classpath:error-messages.properties")
public class MobileAppWsApplication {

  public static void main(String[] args) {
    SpringApplication.run(MobileAppWsApplication.class, args);
    System.out.println("Started..");
  }

  // used for encoding user plain password before persisting to DB
  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // get access to spring application context
  @Bean
  public SpringApplicationContext springApplicationContext() {
    return new SpringApplicationContext();
  }
}
