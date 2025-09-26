package com.ofss.mock.rest.xml.dto;

public class ApiEndpoints {
    private String method;
    private String path;
    private String description;

    // Constructor
    public ApiEndpoints(String method, String path, String description) {
        this.method = method;
        this.path = path;
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}