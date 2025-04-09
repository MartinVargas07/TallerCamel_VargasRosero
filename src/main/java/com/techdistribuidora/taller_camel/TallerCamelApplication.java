package com.techdistribuidora.taller_camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.techdistribuidora.taller_camel", "com.techdistribuidora.routes"})
public class TallerCamelApplication {
    public static void main(String[] args) {
        SpringApplication.run(TallerCamelApplication.class, args);
    }
}
