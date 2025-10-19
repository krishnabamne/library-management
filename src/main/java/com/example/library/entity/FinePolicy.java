package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fine_policy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private double finePerDay;
}
