package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private boolean success;
    private String message;
    private String fileName;
    private String fileUrl;
}