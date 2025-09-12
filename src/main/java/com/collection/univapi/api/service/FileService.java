package com.collection.univapi.api.service;

import com.collection.univapi.api.model.FileMetadata;
import com.collection.univapi.api.model.FileRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.function.BiConsumer;

@Service
public class FileService {

    private static Path getTargetDir(FileRequest request, Path baseDir) {
        String dir = request.getDirectory();

        if (dir == null || dir.isEmpty()) {
            throw new SecurityException("Directory name cannot be empty.");
        }

        if (dir.contains("..") || dir.contains("/") || dir.contains("\\") || dir.startsWith(".")) {
            throw new SecurityException("Invalid directory name");
        }

        String fileName = request.getFileName();

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new SecurityException("File name cannot be empty.");
        }

        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new SecurityException("Invalid file name");
        }

        Path targetDir = baseDir.resolve(dir).normalize();
        if (!targetDir.startsWith(baseDir)) {
            throw new SecurityException("Invalid directory path");
        }

        return targetDir;
    }

    private static Path getTargetFile (FileRequest request, Path baseDir) throws IOException {
        Path targetDir = getTargetDir(request, baseDir);
        Path targetFile = targetDir.resolve(request.getFileName()).normalize();
        if (!targetFile.startsWith(baseDir)) {
            throw new SecurityException("Invalid file path");
        }
        return targetFile;
    }

    private static String transferFile (String sourceDir, String fileName, String targetDir, BiConsumer<Path, Path> operation, String successMessage, String errorMessage) {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();

        FileRequest sourceReq = new FileRequest(fileName, sourceDir, null);
        FileRequest targetReq = new FileRequest(fileName, targetDir, null);

        Path sourcePath;
        Path targetPath;
        try {
            sourcePath = getTargetFile(sourceReq, baseDir);
            targetPath = getTargetFile(targetReq, baseDir);
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        if (!Files.exists(sourcePath)) {
            return "Error: Source file does not exist: " + sourcePath;
        }

        if (sourcePath.equals(targetPath)) {
            return "Error: Source and target paths are the same: " + sourcePath;
        }

        try {
            Files.createDirectories(targetPath.getParent());
            operation.accept(sourcePath, targetPath);
            return successMessage + targetPath;
        } catch (IOException e) {
            return errorMessage + e.getMessage();
        }

    }

    public String saveFile(FileRequest request) throws IOException {
        byte[] data = Base64.getDecoder().decode(request.getBase64Data());

        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();

        Path targetDir = getTargetDir(request, baseDir);
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

    public String moveFile(String sourceDir, String fileName, String targetDir) {
        return transferFile(sourceDir, fileName, targetDir,
                (source, target) -> {
                    try {
                        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                },
                "File moved successfully to: ",
                "Error moving file: ");
    }

    public String copyFile(String sourceDir, String fileName, String targetDir) {
        return transferFile(sourceDir, fileName, targetDir,
                (source, target) -> {
                    try {
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                },
                "File copied successfully to: ",
                "Error copying file: ");
    }

    public FileMetadata getFileMetadata(FileRequest request) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetFile = getTargetFile(request, baseDir);

        if (!Files.exists(targetFile)) {
            throw new FileNotFoundException("File not found: " + targetFile);
        }

        BasicFileAttributes attrs = Files.readAttributes(targetFile, BasicFileAttributes.class);
        long size = attrs.size();
        Instant lastModified = attrs.lastModifiedTime().toInstant();


        String mimeType = Files.probeContentType(targetFile);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // fallback
        }

        String hash = computeFileHash(targetFile, "MD5");

        return new FileMetadata(
                targetFile.getFileName().toString(),
                request.getDirectory(),
                size,
                mimeType,
                lastModified,
                hash
        );
    }

    private String computeFileHash(Path file, String algorithm) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            byte[] hashBytes = digest.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported hash algorithm: " + algorithm, e);
        }
    }

}
