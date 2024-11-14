package io.github.wkktoria.shareall.file;

import io.github.wkktoria.shareall.config.AppConfig;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {
    private final AppConfig appConfig;
    private final Tika tika;

    public FileService(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.tika = new Tika();
    }

    public String saveProfileImage(final String base64Image) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

        File target = new File(appConfig.getFullProfileImagesPath() + "/" + imageName);
        FileUtils.writeByteArrayToFile(target, decodedBytes);

        return imageName;
    }

    public String detectType(byte[] fileArray) {
        return tika.detect(fileArray);
    }

    public void deleteProfileImage(String image) {
        try {
            Files.deleteIfExists(Paths.get(appConfig.getFullProfileImagesPath() + "/" + image));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
