package com.collection.univapi.api.service.metadata;

import com.collection.univapi.api.model.DirectoryRequest;
import com.collection.univapi.api.model.FileMetadata;
import com.collection.univapi.api.model.FileRequest;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.collection.univapi.api.service.util.FileSecurityUtil.getTargetDirectoryOnly;
import static com.collection.univapi.api.service.util.FileSecurityUtil.getTargetFile;

@Service
public class FileMetadataService {



    public FileMetadata getFileMetadata(FileRequest request) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetFile = getTargetFile(request, baseDir);

        if (!Files.exists(targetFile)) {
            throw new FileNotFoundException("File not found: " + targetFile);
        }

        return buildFileMetadata(targetFile, request.getDirectory());
    }

    public List<FileMetadata> listFiles(DirectoryRequest request) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetDir = getTargetDirectoryOnly(request.getDirectory(), baseDir);

        if (!Files.exists(targetDir) || !Files.isDirectory(targetDir)) {
            throw new FileNotFoundException("Directory not found: " + targetDir);
        }

        List<FileMetadata> files = new ArrayList<>();
        try (Stream<Path> paths = Files.list(targetDir)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    files.add(buildFileMetadata(path, request.getDirectory()));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
        return files;
    }


    private FileMetadata buildFileMetadata(Path path, String directory) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        long size = attrs.size();
        Instant lastModified = attrs.lastModifiedTime().toInstant();
        String mimeType = Files.probeContentType(path);
        if (mimeType == null) mimeType = "application/octet-stream";
        String hash = computeFileHash(path, "MD5");

        return new FileMetadata(
                path.getFileName().toString(),
                directory,
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
