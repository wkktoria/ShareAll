package io.github.wkktoria.shareall.file;

import io.github.wkktoria.shareall.config.AppConfig;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class FileServiceTest {
    private FileService fileService;
    private AppConfig appConfig;

    @BeforeEach
    void init() {
        appConfig = new AppConfig();
        appConfig.setUploadPath("uploads-test");

        fileService = new FileService(appConfig);

        new File(appConfig.getUploadPath()).mkdirs();
        new File(appConfig.getFullProfileImagesPath()).mkdirs();
        new File(appConfig.getFullAttachmentsPath()).mkdirs();
    }

    @AfterEach
    void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfig.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfig.getFullAttachmentsPath()));
    }

    @Test
    void detectType_whenPngFileProvided_returnsImagePng() throws IOException {
        ClassPathResource resourceFile = new ClassPathResource("test-png.png");
        byte[] fileArray = FileUtils.readFileToByteArray(resourceFile.getFile());
        String fileType = fileService.detectType(fileArray);
        assertThat(fileType).isEqualToIgnoringCase("image/png");
    }
}