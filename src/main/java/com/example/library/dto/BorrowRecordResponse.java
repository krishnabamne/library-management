package com.example.library.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class BorrowRecordResponse {
    private UUID id;
    private UUID bookId;
    private String bookTitle;
    private UUID borrowerId;
    private String borrowerName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Double fineAmount;
}
