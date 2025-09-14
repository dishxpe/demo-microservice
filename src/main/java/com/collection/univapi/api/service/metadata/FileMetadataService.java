package com.collection.univapi.api.service.metadata;

import com.collection.univapi.api.model.file.DirectoryRequest;
import com.collection.univapi.api.model.file.FileMetadata;
import com.collection.univapi.api.model.file.FileRequest;
import com.collection.univapi.api.model.file.FileSearchRequest;
import com.collection.univapi.api.service.util.FileSecurityUtil;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileMetadataService {

    public List<FileMetadata> searchFiles(FileSearchRequest request, boolean recursive) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetDir = resolveTargetDirectory(request.getDirectory(), baseDir);

        try (Stream<Path> paths = recursive ? Files.walk(targetDir) : Files.list(targetDir)) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            FileMetadata metadata = buildFileMetadata(path, baseDir);

                            if (request.getQuery() != null && !metadata.getFileName().contains(request.getQuery())) {
                                return null;
                            }
                            if (request.getExtension() != null && !metadata.getFileName().endsWith("." + request.getExtension())) {
                                return null;
                            }
                            if (request.getMinSize() != null && metadata.getSize() < request.getMinSize()) {
                                return null;
                            }
                            if (request.getMaxSize() != null && metadata.getSize() > request.getMaxSize()) {
                                return null;
                            }

                            return metadata;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }


    public FileMetadata getFileMetadata(FileRequest request) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetFile = FileSecurityUtil.getTargetFile(request, baseDir);

        if (!Files.exists(targetFile)) {
            throw new FileNotFoundException("File not found: " + targetFile);
        }

        return buildFileMetadata(targetFile, baseDir);
    }

    public List<FileMetadata> listFiles(DirectoryRequest request) throws IOException {
        Path baseDir = Paths.get("uploads").toAbsolutePath().normalize();
        Path targetDir = resolveTargetDirectory(request.getDirectory(), baseDir);

        try (Stream<Path> paths = Files.list(targetDir)) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            return buildFileMetadata(path, baseDir);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    private Path resolveTargetDirectory(String directory, Path baseDir) throws IOException {
        Path targetDir;

        if (directory == null || directory.isBlank()) {
            targetDir = baseDir; // default to base uploads dir
        } else {
            targetDir = FileSecurityUtil.getTargetDirectoryOnly(directory, baseDir);
        }

        if (!Files.exists(targetDir) || !Files.isDirectory(targetDir)) {
            throw new FileNotFoundException("Directory not found: " + targetDir);
        }

        return targetDir;
    }



    private FileMetadata buildFileMetadata(Path path, Path baseDir) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        long size = attrs.size();
        Instant lastModified = attrs.lastModifiedTime().toInstant();
        String mimeType = Files.probeContentType(path);
        if (mimeType == null) mimeType = "application/octet-stream";

        String md5 = computeFileHash(path, "MD5");
        String sha256 = computeFileHash(path, "SHA-256");

        Path relativeDirPath = baseDir.relativize(path.getParent());
        String directory = relativeDirPath.toString().replace("\\", "/");

        return new FileMetadata(
                path.getFileName().toString(),
                directory,
                size,
                mimeType,
                lastModified,
                md5,
                sha256
        );
    }

    public boolean verifyFileHash(Path file, String expectedHash, String algorithm) throws IOException {
        String actualHash = computeFileHash(file, algorithm);
        return actualHash.equalsIgnoreCase(expectedHash);
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
