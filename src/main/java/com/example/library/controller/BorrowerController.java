package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BorrowerRequest;
import com.example.library.dto.BorrowerResponse;
import com.example.library.service.BorrowService;
import com.example.library.service.BorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/borrowers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerService borrowerService;
    private final BorrowService borrowService;

    /**
     * Register a new borrower
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BorrowerResponse>> register(@Valid @RequestBody BorrowerRequest req) {
        BorrowerResponse response = borrowerService.register(req);
        return ResponseEntity.ok(ApiResponse.<BorrowerResponse>builder()
                .success(true)
                .message("Borrower registered successfully")
                .data(response)
                .build());
    }

    /**
     * Get borrow history of a borrower
     */
    @GetMapping("/{id}/records")
    public ResponseEntity<ApiResponse<List<?>>> history(@PathVariable UUID id) {
        List<?> records = borrowService.getBorrowHistory(id);
        return ResponseEntity.ok(ApiResponse.<List<?>>builder()
                .success(true)
                .message("Borrow history fetched successfully")
                .data(records)
                .build());
    }

    /**
     * Get all overdue borrow records
     */
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<?>>> overdueBorrowers() {
        List<?> records = borrowService.getOverdueRecords();
        return ResponseEntity.ok(ApiResponse.<List<?>>builder()
                .success(true)
                .message("Overdue records fetched successfully")
                .data(records)
                .build());
    }
}
