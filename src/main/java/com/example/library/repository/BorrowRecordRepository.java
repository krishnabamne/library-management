package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {
    List<BorrowRecord> findByBorrowerId(UUID borrowerId);
    List<BorrowRecord> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
    List<BorrowRecord> findByReturnDateIsNull();
}
