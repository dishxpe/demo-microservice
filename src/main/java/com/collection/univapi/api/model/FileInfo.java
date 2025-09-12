package com.collection.univapi.api.model;

import java.time.Instant;

public interface FileInfo {
    String getFileName();
    String getDirectory();
    long getSize();
    String getMimeType();
    Instant getLastModified();
    String getHash();
}
