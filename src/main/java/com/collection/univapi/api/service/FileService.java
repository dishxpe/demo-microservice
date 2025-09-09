package com.collection.univapi.api.service;

import com.collection.univapi.api.model.FileRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

@Service
public class FileService {

    public String saveFile(FileRequest request) throws IOException {
        byte[] data = Base64.getDecoder().decode(request.getBase64Data());
        Path dir = Paths.get(request.getDirectory());


        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path path = dir.resolve(request.getFileName());
        Files.write(path, data);

        return "File saved at: " + path.toString();
    }

    public String readFile(FileRequest request) throws IOException {
        Path path = Paths.get(request.getDirectory(), request.getFileName());
        byte[] data = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(data);
    }

    public String deleteFile(FileRequest request) throws IOException {
        Path path = Paths.get(request.getDirectory(), request.getFileName());
        Files.deleteIfExists(path);
        return "File deleted successfully from: " + path;
    }

    public String moveFile(String sourceDir, String fileName, String targetDir) throws IOException {
        Path sourcePath = Paths.get(sourceDir, fileName);
        Path targetPath = Paths.get(targetDir, fileName);
        Path targetDirPath = Paths.get(targetDir);

        if (!Files.exists(sourcePath)) {
            return "Error: Source file does not exist: " + sourcePath;
        }

        if (sourcePath.equals(targetPath)) {
            return "Error: Source and target paths are the same: " + sourcePath;
        }

        if (!Files.exists(targetDirPath)) {
            Files.createDirectories(targetDirPath);
        }

        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "File moved successfully to: " + targetPath;
        } catch (IOException e) {
            return "Error moving file: " + e.getMessage();
        }
    }

    public String copyFile(String sourceDir, String fileName, String targetDir) throws IOException {
        Path sourcePath = Paths.get(sourceDir, fileName);
        Path targetPath = Paths.get(targetDir, fileName);
        Path targetDirPath = Paths.get(targetDir);

        if (!Files.exists(sourcePath)) {
            return "Error: Source file does not exist: " + sourcePath;
        }

        if (sourcePath.equals(targetPath)) {
            return "Error: Cannot copy file to the same location: " + sourcePath;
        }

        if (!Files.exists(targetDirPath)) {
            Files.createDirectories(targetDirPath);
        }

        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "File copied successfully to: " + targetPath;
        } catch (IOException e) {
            return "Error copying file: " + e.getMessage();
        }
    }
}
