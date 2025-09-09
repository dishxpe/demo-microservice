package com.collection.univapi.api.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class FileRequest {
    private String fileName;
    private String directory;
    private String base64Data; // only used when uploading
}
