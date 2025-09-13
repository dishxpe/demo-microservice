package com.collection.univapi.api.controller;

import com.collection.univapi.api.model.file.DirectoryRequest;
import com.collection.univapi.api.model.file.FileMetadata;
import com.collection.univapi.api.model.file.FileRequest;
import com.collection.univapi.api.model.file.FileSearchRequest;
import com.collection.univapi.api.service.metadata.FileMetadataService;
import com.collection.univapi.api.service.util.FileSecurityUtil;
import com.collection.univapi.api.service.storage.LocalFileStorageService;
import com.collection.univapi.api.service.storage.FileTransferService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

    public final FileSecurityUtil fileSecurityUtil;
    public final LocalFileStorageService localFileStorageService;
    public final FileTransferService fileTransferService;
    public final FileMetadataService fileMetadataService;

    public FileController(FileSecurityUtil fileSecurityUtil,
                          LocalFileStorageService localFileStorageService,
                          FileTransferService fileTransferService,
                          FileMetadataService fileMetadataService) {
        this.fileSecurityUtil = fileSecurityUtil;
        this.localFileStorageService = localFileStorageService;
        this.fileTransferService = fileTransferService;
        this.fileMetadataService = fileMetadataService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestBody FileRequest request) throws IOException {
        return localFileStorageService.saveFile(request);
    }

    @PostMapping("/download")
    public String downloadFile(@RequestBody FileRequest request) throws IOException {
        return localFileStorageService.readFile(request);
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestBody FileRequest request) throws IOException {
        return localFileStorageService.deleteFile(request);
    }

    @PostMapping("/move")
    public String moveFile(@RequestParam String sourceDir, @RequestParam String targetDir, @RequestParam String fileName) {
        return fileTransferService.moveFile(sourceDir, fileName, targetDir);
    }

    @PostMapping("/copy")
    public String copyFile(@RequestParam String sourceDir, @RequestParam String targetDir, @RequestParam String fileName) {
        return fileTransferService.copyFile(sourceDir, fileName, targetDir);
    }

    @PostMapping("/metadata")
    public FileMetadata getFileMetadata(@RequestBody FileRequest request) throws IOException {
        return fileMetadataService.getFileMetadata(request);
    }

    @PostMapping("/list")
    public List<FileMetadata> listFiles(@RequestBody DirectoryRequest request) throws IOException {
        return fileMetadataService.listFiles(request);
    }

    @PostMapping("/search")
    public List<FileMetadata> searchFiles(@RequestBody FileSearchRequest request) throws IOException {
        return fileMetadataService.searchFiles(request, request.isRecursive());
    }

    @PostMapping("/verify")
    public boolean verifyFile(@RequestBody FileRequest request, @RequestParam String hash, @RequestParam String algorithm) throws IOException {
        Path baseDir = Path.of("uploads").toAbsolutePath().normalize();
        Path targetFile = FileSecurityUtil.getTargetFile(request, baseDir);
        return fileMetadataService.verifyFileHash(targetFile, hash, algorithm);
    }
}
