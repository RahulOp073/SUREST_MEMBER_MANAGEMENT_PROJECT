package org.surest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;


public class SurestErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    // Private constructor for builder usage
    private SurestErrorResponse() {}

    // Getters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public static class Builder {
        private final SurestErrorResponse response;

        public Builder() {
            response = new SurestErrorResponse();
            response.timestamp = LocalDateTime.now(); // default timestamp
        }


        public Builder status(int status) {
            response.status = status;
            return this;
        }

        public Builder error(String error) {
            response.error = error;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public Builder path(String path) {
            response.path = path;
            return this;
        }

        public SurestErrorResponse build() {
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
