package com.example.library.dto;

import com.example.library.entity.MembershipType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BorrowerResponse {
    private UUID id;
    private String name;
    private String email;
    private MembershipType membershipType;
    private int maxBorrowLimit;
}
