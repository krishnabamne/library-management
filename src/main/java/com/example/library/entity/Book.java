package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String author;

    private String category;

    private String isbn;

    private boolean isAvailable = true;

    private int totalCopies;

    private int availableCopies;

    private boolean deleted = false;
}
