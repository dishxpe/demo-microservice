package com.collection.univapi.api.service.storage;

import com.collection.univapi.api.model.file.FileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

import static com.collection.univapi.api.service.util.FileSecurityUtil.getTargetDir;
import static com.collection.univapi.api.service.util.FileSecurityUtil.getTargetFile;
import static com.collection.univapi.api.service.util.PathUtil.normalize;

@Service
public class LocalFileStorageService {

    public String saveFile(FileRequest request) throws IOException {
        byte[] data;
        try {
            data = Base64.getDecoder().decode(request.getBase64Data());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Base64 data", e);
        }

        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetDir = getTargetDir(request, baseDir);
        Files.createDirectories(targetDir);

        Path targetFile = getTargetFile(request, baseDir);
        Files.write(targetFile, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return normalize(targetFile);
    }


    public String readFile(FileRequest request) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetFile = getTargetFile(request, baseDir);

        if (!Files.exists(targetFile)) {
            throw new FileNotFoundException("File not found: " + normalize(targetFile));
        }

        byte[] data = Files.readAllBytes(targetFile);
        return Base64.getEncoder().encodeToString(data);
    }

    public String deleteFile(FileRequest request) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetFile = getTargetFile(request, baseDir);

        if (!Files.exists(targetFile)) {
            throw new FileNotFoundException("File not found: " + normalize(targetFile));
        }

        Files.delete(targetFile);
        return "File deleted successfully from: " + normalize(targetFile);
    }

}
