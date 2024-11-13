package io.github.wkktoria.shareall.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Configuration
class WebConfig implements WebMvcConfigurer {
    private final AppConfig appConfig;

    WebConfig(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + appConfig.getUploadPath() + "/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }

    private void createNonExistingFolder(final String path) {
        File folder = new File(path);
        boolean folderExists = folder.exists() && folder.isDirectory();
        if (!folderExists) {
            folder.mkdirs();
        }
    }

    @Bean
    CommandLineRunner createUploadFolder() {
        return args -> {
            createNonExistingFolder(appConfig.getUploadPath());
            createNonExistingFolder(appConfig.getFullProfileImagesPath());
            createNonExistingFolder(appConfig.getFullAttachmentsPath());
        };
    }
}
