package io.github.wkktoria.shareall.file;

import io.github.wkktoria.shareall.config.AppConfig;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {
    private final AppConfig appConfig;

    public FileService(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String saveProfileImage(final String base64Image) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

        File target = new File(appConfig.getFullProfileImagesPath() + "/" + imageName);
        FileUtils.writeByteArrayToFile(target, decodedBytes);

        return imageName;
    }
}
