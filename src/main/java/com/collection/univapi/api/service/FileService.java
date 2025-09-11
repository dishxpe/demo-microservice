package com.collection.univapi.api.service;

import com.collection.univapi.api.model.FileRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

@Service
public class FileService {

    private static Path getTargetDir(FileRequest request, Path baseDir) {
        String dir = request.getDirectory();

        if (dir == null || dir.isEmpty()) {
            throw new SecurityException("Directory Name cannot be empty.");
        }

        if (dir.contains("..") || dir.contains("/") || dir.contains("\\") || dir.startsWith(".")) {
            throw new SecurityException("Invalid directory name");
        }

        String fileName = request.getFileName();
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new SecurityException("Invalid file name");
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new SecurityException("File name cannot be empty.");
        }

        Path targetDir = baseDir.resolve(dir).normalize();
        if (!targetDir.startsWith(baseDir)) {
            throw new SecurityException("Invalid directory path");
        }


        return targetDir;
    }

    public String saveFile(FileRequest request) throws IOException {
        byte[] data = Base64.getDecoder().decode(request.getBase64Data());

        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();

        Path targetDir = getTargetDir(request, baseDir);

        Files.createDirectories(targetDir);

        Path targetFile = targetDir.resolve(request.getFileName()).normalize();
        if (!targetFile.startsWith(baseDir)) {
            throw new SecurityException("Invalid file path");
        }

        Files.write(targetFile, data);

        return targetFile.toString();
    }


    public String readFile(FileRequest request) throws IOException {

        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();

        Path targetDir = baseDir.resolve(request.getDirectory()).normalize();
        if (!targetDir.startsWith(baseDir)) {
            throw new SecurityException("Invalid directory path");
        }

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
