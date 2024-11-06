package io.github.wkktoria.shareall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ShareallApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareallApplication.class, args);
    }
}
