package com.collection.univapi.api.controller;

import com.collection.univapi.api.model.DirectoryRequest;
import com.collection.univapi.api.model.FileMetadata;
import com.collection.univapi.api.model.FileRequest;
import com.collection.univapi.api.service.metadata.FileMetadataService;
import com.collection.univapi.api.service.util.FileSecurityUtil;
import com.collection.univapi.api.service.storage.LocalFileStorageService;
import com.collection.univapi.api.service.storage.FileTransferService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public String moveFile(@RequestParam String sourceDir,
                           @RequestParam String targetDir,
                           @RequestParam String fileName) throws IOException {
        return fileTransferService.moveFile(sourceDir, fileName, targetDir);
    }

    @PostMapping("/copy")
    public String copyFile(@RequestParam String sourceDir,
                           @RequestParam String targetDir,
                           @RequestParam String fileName) throws IOException {
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


}
