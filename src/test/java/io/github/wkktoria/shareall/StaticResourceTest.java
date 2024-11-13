package io.github.wkktoria.shareall;

import io.github.wkktoria.shareall.config.AppConfig;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StaticResourceTest {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(appConfig.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfig.getFullAttachmentsPath()));
    }

    @Test
    void checkStaticFolder_whenAppIsInitialized_uploadFolderMustExist() {
        File uploadFolder = new File(appConfig.getUploadPath());
        boolean uploadFolderExists = uploadFolder.exists() && uploadFolder.isDirectory();
        assertThat(uploadFolderExists).isTrue();
    }

    @Test
    void checkStaticFolder_whenAppIsInitialized_profileImageSubfolderMustExist() {
        final String profileImageFolderPath = appConfig.getFullProfileImagesPath();
        File profileImageFolder = new File(profileImageFolderPath);
        boolean profileImageFolderExists = profileImageFolder.exists() && profileImageFolder.isDirectory();
        assertThat(profileImageFolderExists).isTrue();
    }

    @Test
    void checkStaticFolder_whenAppIsInitialized_attachmentsSubFolderMustExist() {
        final String attachmentsFolderPath = appConfig.getFullAttachmentsPath();
        File attachmentsFolder = new File(attachmentsFolderPath);
        boolean attachmentsFolderExists = attachmentsFolder.exists() && attachmentsFolder.isDirectory();
        assertThat(attachmentsFolderExists).isTrue();
    }

    @Test
    void getStaticFile_whenImageExistsInProfileUploadFolder_receiveOk() throws Exception {
        final String filename = "profile-picture.png";
        File source = new ClassPathResource("profile.png").getFile();
        File target = new File(appConfig.getFullProfileImagesPath() + "/" + filename);
        FileUtils.copyFile(source, target);

        mockMvc.perform(get("/images/" + appConfig.getProfileImagesFolder() + "/" + filename))
                .andExpect(status().isOk());
    }

    @Test
    void getStaticFile_whenImageExistsInAttachmentFolder_receiveOk() throws Exception {
        final String filename = "profile-picture.png";
        File source = new ClassPathResource("profile.png").getFile();
        File target = new File(appConfig.getFullAttachmentsPath() + "/" + filename);
        FileUtils.copyFile(source, target);

        mockMvc.perform(get("/images/" + appConfig.getAttachmentsFolder() + "/" + filename))
                .andExpect(status().isOk());
    }

    @Test
    void getStaticFile_whenImageDoesNotExist_receiveNotFound() throws Exception {
        mockMvc.perform(get("/images/" + appConfig.getAttachmentsFolder() + "/there-is-no-such-image.png"))
                .andExpect(status().isNotFound());
    }
}
