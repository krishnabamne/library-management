package com.example.library.repository;

import com.example.library.entity.FinePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinePolicyRepository extends JpaRepository<FinePolicy, Long> {
}
