package com.faithconnect.bookstacksync.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents a Book in the BookStack system.
 * A book is a top-level container that can hold chapters and pages.
 */
@Data
public class Book {
    private Long id;
    private String name;
    private String slug;
    private String description;
    
    @JsonProperty("description_html")
    private String descriptionHtml;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("created_by")
    private User createdBy;
    
    @JsonProperty("updated_by")
    private User updatedBy;
    
    @JsonProperty("owned_by")
    private User ownedBy;
    
    @JsonProperty("default_template_id")
    private Long defaultTemplateId;
    
    private List<Content> contents;
    private List<Tag> tags;
    private Cover cover;
    
    /**
     * Represents a user in the BookStack system.
     */
    @Data
    public static class User {
        private Long id;
        private String name;
        private String slug;
        
        // Default constructor
        public User() {
        }
        
        // Constructor for numeric values
        @JsonCreator
        public static User fromId(Long id) {
            User user = new User();
            user.setId(id);
            return user;
        }
        
        // Alternative constructor for numeric values as integers
        @JsonCreator
        public static User fromInt(Integer id) {
            User user = new User();
            user.setId(id.longValue());
            return user;
        }
    }
    
    /**
     * Represents content within a book, which can be either a chapter or a page.
     */
    @Data
    public static class Content {
        private Long id;
        private String type;
        private String name;
        private String slug;
        
        @JsonProperty("book_id")
        private Long bookId;
        
        @JsonProperty("chapter_id")
        private Long chapterId;
        
        @JsonProperty("created_at")
        private String createdAt;
        
        @JsonProperty("updated_at")
        private String updatedAt;
        
        private String url;
        
        @JsonProperty("draft")
        private Boolean isDraft;
        
        @JsonProperty("template")
        private Boolean isTemplate;
        
        private List<PageSummary> pages;
    }
    
    /**
     * Represents a summary of a page within a chapter.
     */
    @Data
    public static class PageSummary {
        private Long id;
        private String name;
        private String slug;
        
        @JsonProperty("book_id")
        private Long bookId;
        
        @JsonProperty("chapter_id")
        private Long chapterId;
        
        @JsonProperty("draft")
        private Boolean isDraft;
        
        @JsonProperty("template")
        private Boolean isTemplate;
        
        @JsonProperty("created_at")
        private String createdAt;
        
        @JsonProperty("updated_at")
        private String updatedAt;
        
        private String url;
    }
    
    /**
     * Represents a tag associated with a book.
     */
    @Data
    public static class Tag {
        private String name;
        private String value;
        private Integer order;
    }
    
    /**
     * Represents a cover image for a book.
     */
    @Data
    public static class Cover {
        private Long id;
        private String name;
        private String url;
        
        @JsonProperty("created_at")
        private String createdAt;
        
        @JsonProperty("updated_at")
        private String updatedAt;
        
        @JsonProperty("created_by")
        private Long createdBy;
        
        @JsonProperty("updated_by")
        private Long updatedBy;
        
        private String path;
        private String type;
        
        @JsonProperty("uploaded_to")
        private Long uploadedTo;
    }
} 