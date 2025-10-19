package com.example.library.service;

import com.example.library.dto.BorrowerRequest;
import com.example.library.dto.BorrowerResponse;
import com.example.library.entity.Borrower;
import com.example.library.entity.MembershipType;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    /**
     * Register a new borrower
     */
    @Transactional
    public BorrowerResponse register(BorrowerRequest req) {
        MembershipType type = req.getMembershipType() != null
                ? req.getMembershipType()
                : MembershipType.BASIC;

        int borrowLimit = (type == MembershipType.PREMIUM) ? 5 : 2;

        Borrower borrower = Borrower.builder()
                .name(req.getName())
                .email(req.getEmail())
                .membershipType(type)
                .maxBorrowLimit(borrowLimit)
                .build();

        borrowerRepository.save(borrower);

        return BorrowerResponse.builder()
                .id(borrower.getId())
                .name(borrower.getName())
                .email(borrower.getEmail())
                .membershipType(borrower.getMembershipType())
                .maxBorrowLimit(borrower.getMaxBorrowLimit())
                .build();
    }

    /**
     * Fetch borrower by ID
     */
    @Transactional(readOnly = true)
    public Borrower findById(UUID id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
    }
}
