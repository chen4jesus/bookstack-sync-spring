package com.faithconnect.bookstacksync.service;

import com.faithconnect.bookstacksync.model.*;
import com.faithconnect.bookstacksync.util.FileUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class BookStackApiServiceImpl implements BookStackApiService {

    private final RestTemplate restTemplate;  
    private final BookStackConfig sourceConfig;
    private final BookStackConfig destinationConfig;

    public BookStackApiServiceImpl(RestTemplate restTemplate, BookStackConfig sourceConfig, BookStackConfig destinationConfig) {
        this.restTemplate = restTemplate;
        this.sourceConfig = sourceConfig;
        this.destinationConfig = destinationConfig;
    }


    @Override
    public List<Book> listBooks() {
        try {
            log.debug("Listing books from {}", sourceConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(sourceConfig);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<ListResponse<Book>> response = restTemplate.exchange(
                    sourceConfig.getBaseUrl() + "/api/books",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ListResponse<Book>>() {}
            );
            
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception e) {
            log.error("Error listing books: {}", e.getMessage(), e);
            throw new BookStackApiException("Failed to list books", e);
        }
    }

    @Override
    public Book getBook(Long id) {
        try {
            log.debug("Getting book with ID {} from {}", id, sourceConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(sourceConfig);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Book> response = restTemplate.exchange(
                    sourceConfig.getBaseUrl() + "/api/books/" + id,
                    HttpMethod.GET,
                    requestEntity,
                    Book.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error getting book with ID {}: {}", id, e.getMessage(), e);
            throw new BookStackApiException("Failed to get book with ID " + id, e);
        }
    }

    @Override
    public Book createBook(Book book) {
        try {
            log.debug("Creating book in {}", destinationConfig.getBaseUrl());

            // Create headers for the request
            HttpHeaders headers = createHeaders(destinationConfig);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Set Content-Type to multipart/form-data

            // Construct the MultiValueMap to hold form data
            MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
            multipartRequest.add("name", book.getName()); // Add individual fields
            multipartRequest.add("slug", book.getSlug());
            multipartRequest.add("description", book.getDescription());
            
            if (book.getDefaultTemplateId() != null) {
                multipartRequest.add("default_template_id", book.getDefaultTemplateId());
            }

            // Add tags (if they are present)
            if (book.getTags() != null && !book.getTags().isEmpty()) {
                // Convert tags to JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                String tagsJson = objectMapper.writeValueAsString(book.getTags());
                multipartRequest.add("tags", tagsJson);
            }

            // Add cover image (if available)
            if (book.getCover() != null && book.getImage() != null) {
                try {
                    // Create a temporary file for the image
                    Path tempFile = Files.createTempFile("book_cover_", ".jpg");
                    Files.write(tempFile, book.getImage().getBytes());
                    
                    // Create a FileSystemResource from the temp file
                    FileSystemResource fileResource = new FileSystemResource(tempFile.toFile());
                    
                    // Add the image to the request
                    multipartRequest.add("image", fileResource);
                    
                    // Register the temp file for deletion when the JVM exits
                    tempFile.toFile().deleteOnExit();
                } catch (IOException e) {
                    log.error("Error creating temporary file for image: {}", e.getMessage());
                    throw new BookStackApiException("Failed to process image for book", e);
                }
            }

            // Create an HttpEntity with the form data and headers
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, headers);

            try {
                // Send the POST request
                ResponseEntity<Book> response = restTemplate.exchange(
                        destinationConfig.getBaseUrl() + "/api/books",
                        HttpMethod.POST,
                        requestEntity,
                        Book.class
                );
                
                return response.getBody(); // Return the created book
            } catch (HttpStatusCodeException e) {
                // Log the response body for better debugging
                log.error("API error response: {}", e.getResponseBodyAsString());
                throw e;
            }

        } catch (Exception e) {
            log.error("Error creating book: {}", e.getMessage(), e);
            throw new BookStackApiException("Failed to create book", e);
        }
    }


    @Override
    public Book updateBook(Long id, Book book) {
        return null;
    }

    @Override
    public boolean deleteBook(Long id) {
        return false;
    }

    @Override
    public List<Chapter> listChapters(Long bookId) {
        try {
            log.debug("Listing chapters for book ID {} from {}", bookId, sourceConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(sourceConfig);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<ListResponse<Chapter>> response = restTemplate.exchange(
                    sourceConfig.getBaseUrl() + "/api/books/" + bookId + "/chapters",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ListResponse<Chapter>>() {}
            );
            
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception e) {
            log.error("Error listing chapters for book ID {}: {}", bookId, e.getMessage(), e);
            throw new BookStackApiException("Failed to list chapters for book ID " + bookId, e);
        }
    }

    @Override
    public Chapter getChapter(Long id) {
        try {
            log.debug("Getting chapter with ID {} from {}", id, sourceConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(sourceConfig);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Chapter> response = restTemplate.exchange(
                    sourceConfig.getBaseUrl() + "/api/chapters/" + id,
                    HttpMethod.GET,
                    requestEntity,
                    Chapter.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error getting chapter with ID {}: {}", id, e.getMessage(), e);
            throw new BookStackApiException("Failed to get chapter with ID " + id, e);
        }
    }

    @Override
    public Chapter createChapter(Chapter chapter) {
        try {
            log.debug("Creating chapter in {}", destinationConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(destinationConfig);
            HttpEntity<Chapter> requestEntity = new HttpEntity<>(chapter, headers);
            
            ResponseEntity<Chapter> response = restTemplate.exchange(
                    destinationConfig.getBaseUrl() + "/api/chapters",
                    HttpMethod.POST,
                    requestEntity,
                    Chapter.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating chapter: {}", e.getMessage(), e);
            throw new BookStackApiException("Failed to create chapter", e);
        }
    }

    @Override
    public List<Page> listPages(Long bookId) {
        try {
            log.debug("Listing pages for book ID {} from {}", bookId, sourceConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(sourceConfig);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<ListResponse<Page>> response = restTemplate.exchange(
                    sourceConfig.getBaseUrl() + "/api/books/" + bookId + "/pages",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ListResponse<Page>>() {}
            );
            
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception e) {
            log.error("Error listing pages for book ID {}: {}", bookId, e.getMessage(), e);
            throw new BookStackApiException("Failed to list pages for book ID " + bookId, e);
        }
    }

    @Override
    public List<Page> listChapterPages(Long chapterId) {
        return List.of();
    }

    @Override
    public Page getPage(Long id) {
        try {
            log.debug("Getting page with ID {} from {}", id, sourceConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(sourceConfig);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Page> response = restTemplate.exchange(
                    sourceConfig.getBaseUrl() + "/api/pages/" + id,
                    HttpMethod.GET,
                    requestEntity,
                    Page.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error getting page with ID {}: {}", id, e.getMessage(), e);
            throw new BookStackApiException("Failed to get page with ID " + id, e);
        }
    }

    @Override
    public Page createPage(Page page) {
        try {
            log.debug("Creating page in {}", destinationConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(destinationConfig);
            HttpEntity<Page> requestEntity = new HttpEntity<>(page, headers);
            
            ResponseEntity<Page> response = restTemplate.exchange(
                    destinationConfig.getBaseUrl() + "/api/pages",
                    HttpMethod.POST,
                    requestEntity,
                    Page.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating page: {}", e.getMessage(), e);
            throw new BookStackApiException("Failed to create page", e);
        }
    }

    @Override
    public Page updatePage(Long id, Page page) {
        return null;
    }

    @Override
    public boolean deletePage(Long id) {
        return false;
    }

    @Override
    public byte[] exportPageAsPdf(Long id) {
        return new byte[0];
    }

    @Override
    public String exportPageAsHtml(Long id) {
        return "";
    }

    @Override
    public String exportPageAsText(Long id) {
        return "";
    }

    @Override
    public String exportPageAsMarkdown(Long id) {
        return "";
    }

    @Override
    public boolean verifyCredentials() {
        try {
            log.info("Verifying credentials for {}", sourceConfig.getBaseUrl());
            listBooks();
            log.info("Successfully verified credentials for {}", sourceConfig.getBaseUrl());
            return true;
        } catch (Exception e) {
            log.error("Failed to verify credentials for {}: {}", sourceConfig.getBaseUrl(), e.getMessage(), e);
            if (e instanceof HttpClientErrorException.Unauthorized) {
                throw new BookStackApiException("Invalid API credentials for " + sourceConfig.getBaseUrl(), e);
            }
            throw new BookStackApiException("Failed to verify credentials for " + sourceConfig.getBaseUrl(), e);
        }
    }

    @Override
    public void syncBook(Long sourceBookId) {
        try {
//            log.info("Starting book sync process...");
//            log.info("Source server: {}", sourceConfig.getBaseUrl());
//            log.info("Destination server: {}", destinationConfig.getBaseUrl());

            // Verify source credentials
//            log.info("Verifying source credentials...");
            verifyCredentials();
            
            // Verify destination credentials
//            log.info("Verifying destination credentials...");
            verifyDestinationCredentials();

            // Get the source book
//            log.info("Reading source book...");
            Book sourceBook = getBook(sourceBookId);
            
            // Create the book in the destination
            log.info("Creating book in destination...");
            Book destBook = createBook(createBookCopy(sourceBook));

            // Process chapters and pages
            for (Book.Content content : sourceBook.getContents()) {
                if ("chapter".equals(content.getType())) {
                    // Get the chapter from the source
                    Chapter chapter = getChapter(content.getId());
                    
                    // Create the chapter in the destination
                    Chapter destChapter = createChapter(createChapterCopy(chapter, destBook.getId()));

                    // Process pages in this chapter
                    for (Book.PageSummary pageSummary : chapter.getPages()) {
                        // Get the page from the source
                        Page page = getPage(pageSummary.getId());
                        
                        // Create the page in the destination
                        createPage(createPageCopy(page, destBook.getId(), destChapter.getId()));
                    }
                } else if ("page".equals(content.getType())) {
                    // Get the page from the source
                    Page page = getPage(content.getId());
                    
                    // Create the page in the destination
                    createPage(createPageCopy(page, destBook.getId(), null));
                }
            }
            
            log.info("Book sync completed successfully");
        } catch (Exception e) {
            log.error("Error syncing book: {}", e.getMessage(), e);
            throw new BookStackApiException("Failed to sync book", e);
        }
    }

    private boolean verifyDestinationCredentials() {
        try {
            log.info("Verifying credentials for {}", destinationConfig.getBaseUrl());
            HttpHeaders headers = createHeaders(destinationConfig);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<ListResponse<Book>> response = restTemplate.exchange(
                    destinationConfig.getBaseUrl() + "/api/books",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ListResponse<Book>>() {}
            );
            
            log.info("Successfully verified credentials for {}", destinationConfig.getBaseUrl());
            return true;
        } catch (Exception e) {
            log.error("Failed to verify credentials for {}: {}", destinationConfig.getBaseUrl(), e.getMessage(), e);
            if (e instanceof HttpClientErrorException.Unauthorized) {
                throw new BookStackApiException("Invalid API credentials for " + destinationConfig.getBaseUrl(), e);
            }
            throw new BookStackApiException("Failed to verify credentials for " + destinationConfig.getBaseUrl(), e);
        }
    }

    private HttpHeaders createHeaders(BookStackConfig config) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + config.getTokenId() + ":" + config.getTokenSecret());
        return headers;
    }

    private Book createBookCopy(Book sourceBook) {
        Book book = new Book();
        book.setName(sourceBook.getName());
        book.setSlug(sourceBook.getSlug());
        book.setDescription(sourceBook.getDescription());
        book.setDescriptionHtml(sourceBook.getDescriptionHtml());
        book.setContents(Collections.emptyList());
        book.setTags(sourceBook.getTags());
        
        book.setDefaultTemplateId(sourceBook.getDefaultTemplateId());
        
        if (sourceBook.getCover() != null) {
            Book.Cover cover = new Book.Cover();
            cover.setName(sourceBook.getCover().getName());
            cover.setUrl(sourceBook.getCover().getUrl());
            cover.setPath(sourceBook.getCover().getPath());
            cover.setType(sourceBook.getCover().getType());
            book.setCover(cover);
            try {
                // Download the image as binary data
                byte[] imageData = new FileUtil().downloadFile(sourceBook.getCover().getUrl());
                book.setImageData(imageData);
            } catch (IOException e) {
                log.error("Error downloading cover image: {}", e.getMessage(), e);
                throw new BookStackApiException("Failed to download cover image", e);
            }
        }
        
        return book;
    }



    private Chapter createChapterCopy(Chapter sourceChapter, Long destBookId) {
        Chapter chapter = new Chapter();
        chapter.setBookId(destBookId);
        chapter.setName(sourceChapter.getName());
        chapter.setSlug(sourceChapter.getSlug());
        chapter.setDescription(sourceChapter.getDescription());
        chapter.setPriority(sourceChapter.getPriority());
        chapter.setPages(Collections.emptyList());
        chapter.setTags(sourceChapter.getTags());
        return chapter;
    }

    private Page createPageCopy(Page sourcePage, Long destBookId, Long destChapterId) {
        Page page = new Page();
        page.setBookId(destBookId);
        page.setChapterId(destChapterId);
        page.setName(sourcePage.getName());
        page.setSlug(sourcePage.getSlug());
        page.setHtml(sourcePage.getHtml());
        page.setMarkdown(sourcePage.getMarkdown());
        page.setPriority(sourcePage.getPriority());
        page.setIsDraft(sourcePage.getIsDraft());
        page.setIsTemplate(sourcePage.getIsTemplate());
        page.setTags(sourcePage.getTags());
        return page;
    }

    public static class BookStackApiException extends RuntimeException {
        public BookStackApiException(String message) {
            super(message);
        }

        public BookStackApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 