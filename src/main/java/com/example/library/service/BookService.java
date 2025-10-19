package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.entity.Book;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse addOrUpdate(BookRequest req) {
        // Validate request data
        validateBookRequestForCreate(req);

        try {
            // Trim title and ISBN for clean data
            String title = req.getTitle().trim();
            String isbn = req.getIsbn() != null ? req.getIsbn().trim() : null;

            // Check duplicate ISBN
            if (StringUtils.hasText(isbn)) {
                Optional<Book> byIsbn = bookRepository.findByIsbnAndDeletedFalse(isbn);
                if (byIsbn.isPresent()) {
                    throw new DuplicateResourceException("A book with this ISBN already exists.");
                }
            }

            // Check if the same title already exists
            Optional<Book> existing = bookRepository.findByTitleAndDeletedFalse(title);
            Book book;

            if (existing.isPresent()) {
                // If book exists, add new copies to it
                book = existing.get();
                int addedCopies = req.getTotalCopies();
                book.setTotalCopies(book.getTotalCopies() + addedCopies);
                book.setAvailableCopies(book.getAvailableCopies() + addedCopies);
                book.setAvailable(book.getAvailableCopies() > 0);
            } else {
                // If book does not exist, create a new entry
                book = Book.builder()
                        .title(title)
                        .author(req.getAuthor())
                        .category(req.getCategory())
                        .isbn(isbn)
                        .totalCopies(req.getTotalCopies())
                        .availableCopies(req.getTotalCopies())
                        .isAvailable(req.getTotalCopies() > 0)
                        .deleted(false)
                        .build();
            }

            // Save the book in database
            Book saved = bookRepository.save(book);
            return toDto(saved);

        } catch (DataIntegrityViolationException ex) {
            // Handle unique constraint violations
            throw new DuplicateResourceException("A book with the same unique field already exists.");
        } catch (DuplicateResourceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save book: " + ex.getMessage(), ex);
        }
    }

    public Page<BookResponse> list(String category, Boolean available, int page, int size, String sortBy) {
        // Apply default pagination and sorting values
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (!StringUtils.hasText(sortBy)) sortBy = "title";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));

        List<Book> filteredBooks;

        // Filter by category
        if (StringUtils.hasText(category)) {
            filteredBooks = bookRepository.findByCategoryAndDeletedFalse(category.trim());
        } else {
            filteredBooks = bookRepository.findAll().stream()
                    .filter(b -> !b.isDeleted())
                    .collect(Collectors.toList());
        }

        // Filter by availability
        if (available != null) {
            filteredBooks = filteredBooks.stream()
                    .filter(b -> available.equals(b.isAvailable()))
                    .collect(Collectors.toList());
        }

        // Handle pagination manually
        int start = Math.min((int) pageRequest.getOffset(), filteredBooks.size());
        int end = Math.min(start + pageRequest.getPageSize(), filteredBooks.size());

        // Convert entity list to response list
        List<BookResponse> content = filteredBooks.subList(start, end).stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, filteredBooks.size());
    }

    public BookResponse getById(UUID id) {
        // Check for valid ID
        if (id == null) throw new IllegalArgumentException("Book ID must be provided");

        // Find book by ID or throw exception if not found
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return toDto(book);
    }

    @Transactional
    public BookResponse update(UUID id, BookRequest req) {
        // Validate ID
        if (id == null) throw new IllegalArgumentException("Book ID must be provided");

        // Find book by ID
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Update fields if provided
        if (StringUtils.hasText(req.getTitle())) book.setTitle(req.getTitle().trim());
        if (StringUtils.hasText(req.getAuthor())) book.setAuthor(req.getAuthor());
        if (StringUtils.hasText(req.getCategory())) book.setCategory(req.getCategory());
        if (StringUtils.hasText(req.getIsbn())) book.setIsbn(req.getIsbn().trim());

        // Update copies if value provided
        if (req.getTotalCopies() > 0) {
            int diff = req.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(req.getTotalCopies());
            book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        }

        // Update availability flag
        book.setAvailable(book.getAvailableCopies() > 0);

        // Save and return updated record
        Book updated = bookRepository.save(book);
        return toDto(updated);
    }

    @Transactional
    public void softDelete(UUID id) {
        // Validate ID
        if (id == null) throw new IllegalArgumentException("Book ID must be provided");

        // Find book by ID
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Mark book as deleted instead of removing from DB
        book.setDeleted(true);
        bookRepository.save(book);
    }

    // Convert Book entity to BookResponse DTO
    private BookResponse toDto(Book b) {
        return BookResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .author(b.getAuthor())
                .category(b.getCategory())
                .available(b.isAvailable())
                .totalCopies(b.getTotalCopies())
                .availableCopies(b.getAvailableCopies())
                .build();
    }

    // Validate input request before creating or updating book
    private void validateBookRequestForCreate(BookRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is missing");
        if (!StringUtils.hasText(req.getTitle())) throw new IllegalArgumentException("Title must be provided");
        if (req.getTotalCopies() <= 0) throw new IllegalArgumentException("Total copies must be at least 1");
    }
}
