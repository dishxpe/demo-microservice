package com.collection.univapi.api.service.storage;

import com.collection.univapi.api.model.file.FileRequest;
import com.collection.univapi.api.service.util.PathUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.BiConsumer;

import static com.collection.univapi.api.service.util.FileSecurityUtil.getTargetFile;

@Service
public class FileTransferService {
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
            return "Error: Source file does not exist: " + PathUtil.normalize(sourcePath);
        }

        if (sourcePath.equals(targetPath)) {
            return "Error: Source and target paths are the same: " + PathUtil.normalize(sourcePath);
        }

        try {
            Files.createDirectories(targetPath.getParent());
            operation.accept(sourcePath, targetPath);
            return successMessage + PathUtil.normalize(sourcePath);
        } catch (IOException e) {
            return errorMessage + e.getMessage();
        }

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
}
