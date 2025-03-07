package com.faithconnect.bookstacksync.service;

import com.faithconnect.bookstacksync.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookStackApiServiceImpl implements BookStackApiService {

    private final RestTemplate restTemplate;  
    private final BookStackConfig sourceConfig;
    private final BookStackConfig destinationConfig;

    @Autowired
    public BookStackApiServiceImpl(BookStackConfig sourceConfig, BookStackConfig destinationConfig, RestTemplate restTemplate) {
        this.sourceConfig = sourceConfig;
        this.destinationConfig = destinationConfig;
        this.restTemplate = restTemplate;
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
            HttpHeaders headers = createHeaders(destinationConfig);
            HttpEntity<Book> requestEntity = new HttpEntity<>(book, headers);
            
            ResponseEntity<Book> response = restTemplate.exchange(
                    destinationConfig.getBaseUrl() + "/api/books",
                    HttpMethod.POST,
                    requestEntity,
                    Book.class
            );
            
            return response.getBody();
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
            log.info("Starting book sync process...");
            log.info("Source server: {}", sourceConfig.getBaseUrl());
            log.info("Destination server: {}", destinationConfig.getBaseUrl());

            // Verify source credentials
            log.info("Verifying source credentials...");
            verifyCredentials();
            
            // Verify destination credentials
            log.info("Verifying destination credentials...");
            verifyDestinationCredentials();

            // Get the source book
            log.info("Reading source book...");
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