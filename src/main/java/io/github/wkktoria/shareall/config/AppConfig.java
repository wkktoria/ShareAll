package io.github.wkktoria.shareall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "shareall")
public class AppConfig {
    private String uploadPath;

    private String profileImagesFolder = "profile";

    private String attachmentsFolder = "attachments";

    public String getFullProfileImagesPath() {
        return uploadPath + "/" + profileImagesFolder;
    }

    public String getFullAttachmentsPath() {
        return uploadPath + "/" + attachmentsFolder;
    }
}
