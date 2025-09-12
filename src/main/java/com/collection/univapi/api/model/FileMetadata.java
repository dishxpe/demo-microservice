package com.collection.univapi.api.model;

import lombok.*;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class FileMetadata implements FileInfo {
    private String fileName;
    private String directory;
    private long size;
    private String mimeType;
    private Instant lastModified;
    private String hash;
}
