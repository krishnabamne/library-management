package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Create or update book
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.addOrUpdate(request);
        return ResponseEntity.ok(ApiResponse.<BookResponse>builder()
                .success(true)
                .message("Book added successfully")
                .data(response)
                .build());
    }

    // Get all books with optional filters
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getAllBooks(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "available", required = false) Boolean available,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "title") String sortBy) {

        Page<BookResponse> books = bookService.list(category, available, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.<Page<BookResponse>>builder()
                .success(true)
                .message("Books fetched successfully")
                .data(books)
                .build());
    }

    // Get book by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable UUID id) {
        BookResponse response = bookService.getById(id);
        return ResponseEntity.ok(ApiResponse.<BookResponse>builder()
                .success(true)
                .message("Book fetched successfully")
                .data(response)
                .build());
    }

    // Update book
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody BookRequest request) {

        BookResponse updated = bookService.update(id, request);
        return ResponseEntity.ok(ApiResponse.<BookResponse>builder()
                .success(true)
                .message("Book updated successfully")
                .data(updated)
                .build());
    }

    // Soft delete book
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBook(@PathVariable UUID id) {
        bookService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Book deleted successfully")
                .data("Deleted")
                .build());
    }
}
