package com.collection.univapi.api.service.storage;

import com.collection.univapi.api.model.FileRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static com.collection.univapi.api.service.util.FileSecurityUtil.getTargetDir;
import static com.collection.univapi.api.service.util.FileSecurityUtil.getTargetFile;

@Service
public class LocalFileStorageService {
    public String saveFile(FileRequest request) throws IOException {
        byte[] data = Base64.getDecoder().decode(request.getBase64Data());

        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();

        Files.createDirectories(getTargetDir(request, baseDir));

        Path targetFile = getTargetFile(request, baseDir);
        Files.write(targetFile, data);

        return targetFile.toString();
    }


    public String readFile(FileRequest request) throws IOException {

        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetFile = getTargetFile(request, baseDir);

        byte[] data = Files.readAllBytes(targetFile);
        return Base64.getEncoder().encodeToString(data);
    }

    public String deleteFile(FileRequest request) throws IOException {

        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetFile = getTargetFile(request, baseDir);

        Files.deleteIfExists(targetFile);
        return "File deleted successfully from: " + targetFile;
    }
}
