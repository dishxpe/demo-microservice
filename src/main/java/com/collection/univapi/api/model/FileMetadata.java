package com.collection.univapi.api.model;

public class FileMetadata implements FileInfo {
    private String fileName;
    private String directory;
    private long size;
    private String mimeType;
    private java.time.Instant lastModified;
    private String hash;

    public FileMetadata(String fileName, String directory, long size, String mimeType, java.time.Instant lastModified, String hash) {
        this.fileName = fileName;
        this.directory = directory;
        this.size = size;
        this.mimeType = mimeType;
        this.lastModified = lastModified;
        this.hash = hash;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getDirectory() {
        return directory;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public java.time.Instant getLastModified() {
        return lastModified;
    }

    @Override
    public String getHash() {
        return hash;
    }
}