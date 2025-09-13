package com.collection.univapi.api.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchRequest {
    private String directory;        // required
    private String query;            // optional: substring in filename
    private String extension;        // optional: filter by extension, e.g., "pdf"
    private Long minSize;            // optional: bytes
    private Long maxSize;            // optional: bytes
    private boolean recursive = false; // default: false
}
