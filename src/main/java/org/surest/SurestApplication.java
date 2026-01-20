package org.surest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class SurestApplication {

    public static void main(String[] args) {

        SpringApplication.run(SurestApplication.class, args);
    }

}
