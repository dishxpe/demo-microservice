package com.collection.univapi.api.service.util;

import com.collection.univapi.api.model.file.FileRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class FileSecurityUtil {

    public static Path getTargetDir(FileRequest request, Path baseDir) {
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

    public static Path getTargetDirectoryOnly(String dir, Path baseDir) {
        if (dir == null || dir.isEmpty()) {
            throw new SecurityException("Directory name cannot be empty.");
        }

        if (dir.contains("..") || dir.contains("/") || dir.contains("\\") || dir.startsWith(".")) {
            throw new SecurityException("Invalid directory name");
        }

        Path targetDir = baseDir.resolve(dir).normalize();
        if (!targetDir.startsWith(baseDir)) {
            throw new SecurityException("Invalid directory path");
        }

        return targetDir;
    }


    public static Path getTargetFile(FileRequest request, Path baseDir) throws IOException {
        Path targetDir = getTargetDir(request, baseDir);
        Path targetFile = targetDir.resolve(request.getFileName()).normalize();
        if (!targetFile.startsWith(baseDir)) {
            throw new SecurityException("Invalid file path");
        }
        return targetFile;
    }



}
