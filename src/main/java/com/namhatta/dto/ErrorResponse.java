package com.namhatta.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private List<String> details;
    private String timestamp;
    
    @JsonIgnore
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder().timestamp(Instant.now().toString());
    }
}