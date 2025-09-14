package com.collection.univapi.api.model.file;

import java.time.Instant;

public interface FileInfo {
    String getFileName();
    String getDirectory();
    long getSize();
    String getMimeType();
    Instant getLastModified();
    String getMd5();
    String getSha256();
}
