package com.faithconnect.bookstacksync.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents a Page in the BookStack system.
 * A page is a content item that can be located within a book or chapter.
 */
@Data
public class Page {
    private Long id;
    
    @JsonProperty("book_id")
    private Long bookId;
    
    @JsonProperty("chapter_id")
    private Long chapterId;
    
    private String name;
    private String slug;
    private String html;
    private String markdown;
    private Integer priority;
    
    @JsonProperty("draft")
    private Boolean isDraft;
    
    @JsonProperty("template")
    private Boolean isTemplate;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("created_by")
    private Book.User createdBy;
    
    @JsonProperty("updated_by")
    private Book.User updatedBy;
    
    private List<Book.Tag> tags;
    
    /**
     * The URL to access this page in the BookStack UI.
     */
    private String url;
    
    /**
     * The revision count for this page.
     */
    @JsonProperty("revision_count")
    private Integer revisionCount;
    
    /**
     * The ID of the owner of this page.
     */
    @JsonProperty("owned_by")
    private Book.User ownedBy;
} 