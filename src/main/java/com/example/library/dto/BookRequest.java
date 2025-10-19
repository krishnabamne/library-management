package com.example.library.dto;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String author;
    private String category;
    private String isbn;
    private int totalCopies;
}
