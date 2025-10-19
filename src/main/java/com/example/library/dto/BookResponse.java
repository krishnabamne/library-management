package com.example.library.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BookResponse {
    private UUID id;
    private String title;
    private String author;
    private String category;
    private boolean available;
    private int totalCopies;
    private int availableCopies;
}
