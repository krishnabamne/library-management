package com.example.library.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BorrowRequest {
    private UUID borrowerId;
    private UUID bookId;
}
