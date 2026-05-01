package com.tealedge.backend;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        configureRenderDatabase();
        SpringApplication.run(BackendApplication.class, args);
    }

    private static void configureRenderDatabase() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank() || System.getenv("SPRING_DATASOURCE_URL") != null) {
            return;
        }

        URI uri = URI.create(databaseUrl);
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        System.setProperty("spring.datasource.url", "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath());

        String userInfo = uri.getUserInfo();
        if (userInfo == null) {
            return;
        }

        String[] credentials = userInfo.split(":", 2);
        System.setProperty("spring.datasource.username", decode(credentials[0]));
        if (credentials.length > 1) {
            System.setProperty("spring.datasource.password", decode(credentials[1]));
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
