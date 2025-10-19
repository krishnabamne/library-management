package com.example.library.dto;

import com.example.library.entity.MembershipType;
import lombok.Data;

@Data
public class BorrowerRequest {
    private String name;
    private String email;
    private MembershipType membershipType;
}
