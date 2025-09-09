package com.collection.univapi.api.controller;

import com.collection.univapi.api.model.FileRequest;
import com.collection.univapi.api.service.FileService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileController {

    public final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestBody FileRequest request) throws IOException {
        return fileService.saveFile(request);
    }

    @PostMapping("/download")
    public String downloadFile(@RequestBody FileRequest request) throws IOException {
        return fileService.readFile(request);
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestBody FileRequest request) throws IOException {
        return fileService.deleteFile(request);
    }

    @PostMapping("/move")
    public String moveFile(@RequestParam String sourceDir, @RequestParam String targetDir, @RequestParam String fileName) throws IOException {
        return fileService.moveFile(sourceDir, fileName, targetDir);
    }

    @PostMapping("/copy")
    public String copyFile(@RequestParam String sourceDir, @RequestParam String targetDir, @RequestParam String fileName) throws IOException {
        return fileService.copyFile(sourceDir, fileName, targetDir);
    }
}
