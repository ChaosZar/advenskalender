package org.chaos.advenskalender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdvenskalenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvenskalenderApplication.class, args);
    }

}
