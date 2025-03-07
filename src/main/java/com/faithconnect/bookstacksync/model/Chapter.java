package com.faithconnect.bookstacksync.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Chapter {
    private Long id;
    
    @JsonProperty("book_id")
    private Long bookId;
    
    private String name;
    private String slug;
    private String description;
    private Integer priority;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("created_by")
    private Book.User createdBy;
    
    @JsonProperty("updated_by")
    private Book.User updatedBy;
    
    private List<Book.PageSummary> pages;
    private List<Book.Tag> tags;
} 