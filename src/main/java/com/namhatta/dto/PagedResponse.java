package com.namhatta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper for API endpoints
 * Maintains compatibility with Node.js API response format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    /**
     * List of data items for current page
     */
    private List<T> data;
    
    /**
     * Current page number (1-based)
     */
    private int page;
    
    /**
     * Number of items per page
     */
    private int size;
    
    /**
     * Total number of items across all pages
     */
    private int total;
    
    /**
     * Total number of pages
     */
    private int totalPages;
    
    /**
     * Whether there is a next page
     */
    public boolean hasNext() {
        return page < totalPages;
    }
    
    /**
     * Whether there is a previous page
     */
    public boolean hasPrevious() {
        return page > 1;
    }
    
    /**
     * Whether this is the first page
     */
    public boolean isFirst() {
        return page == 1;
    }
    
    /**
     * Whether this is the last page
     */
    public boolean isLast() {
        return page == totalPages;
    }
    
    /**
     * Whether the result set is empty
     */
    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }
    
    /**
     * Number of items in current page
     */
    public int getNumberOfElements() {
        return data == null ? 0 : data.size();
    }
}