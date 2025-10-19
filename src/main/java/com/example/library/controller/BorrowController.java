package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BorrowRequest;
import com.example.library.dto.BorrowRecordResponse;
import com.example.library.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/borrow", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    // Borrow a book
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> borrow(@Valid @RequestBody BorrowRequest req) {
        UUID borrowerId = req.getBorrowerId();
        UUID bookId = req.getBookId();

        var record = borrowService.borrowBook(borrowerId, bookId);
        BorrowRecordResponse response = borrowService.toDto(record);

        return ResponseEntity.ok(ApiResponse.<BorrowRecordResponse>builder()
                .success(true)
                .message("Book borrowed successfully")
                .data(response)
                .build());
    }

    // Return a borrowed book
    @PostMapping(path = "/return", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> returnBook(@Valid @RequestBody BorrowRequest req) {
        UUID borrowerId = req.getBorrowerId();
        UUID bookId = req.getBookId();

        var record = borrowService.returnBook(borrowerId, bookId);
        BorrowRecordResponse response = borrowService.toDto(record);

        return ResponseEntity.ok(ApiResponse.<BorrowRecordResponse>builder()
                .success(true)
                .message("Book returned successfully")
                .data(response)
                .build());
    }

    // List all active borrow records
    @GetMapping("/records/active")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> activeRecords() {
        List<BorrowRecordResponse> records = borrowService.getActiveRecords();
        return ResponseEntity.ok(ApiResponse.<List<BorrowRecordResponse>>builder()
                .success(true)
                .message("Active borrow records fetched successfully")
                .data(records)
                .build());
    }
}
