package com.bloxbean.algodea.idea.codegen.service.util;

public class FileContent {
    private String fileName;
    private String content;
    private String extension;
    private boolean skipIfExists;

    public FileContent(String fileName, String extension, String content, boolean skipIfExists) {
        this.fileName = fileName;
        this.extension = extension;
        this.content = content;
        this.skipIfExists = skipIfExists;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }

    public String getContent() {
        return content;
    }

    public boolean isSkipIfExists() {
        return skipIfExists;
    }

}
