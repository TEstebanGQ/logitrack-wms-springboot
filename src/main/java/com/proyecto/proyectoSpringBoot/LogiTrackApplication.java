package com.proyecto.proyectoSpringBoot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class LogiTrackApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
    }

    public static void main(String[] args) {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl != null) {
            if (dbUrl.startsWith("postgresql://")) {
                dbUrl = "jdbc:" + dbUrl;
            }
            System.setProperty("spring.datasource.url", dbUrl);
        }
        SpringApplication.run(LogiTrackApplication.class, args);
    }
}

