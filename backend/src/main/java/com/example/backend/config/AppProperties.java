package com.example.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Upload upload = new Upload();
    private final Swagger swagger = new Swagger();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMs;
        private long refreshExpirationMs;
    }

    @Getter
    @Setter
    public static class Upload {
        private String dir;
        private long maxSize;
    }

    @Getter
    @Setter
    public static class Swagger {
        private String title;
        private String description;
        private String version;
        private String contactName;
        private String contactEmail;
        private String contactUrl;
    }
}
