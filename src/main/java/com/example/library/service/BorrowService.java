package com.example.library.service;

import com.example.library.dto.BorrowRecordResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Borrower;
import com.example.library.entity.FinePolicy;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.BorrowerRepository;
import com.example.library.repository.FinePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final BorrowRecordRepository recordRepository;
    private final FinePolicyRepository finePolicyRepository;

    private static final double DEFAULT_FINE_PER_DAY = 10.0;

    /**
     * Borrow a book for a borrower
     */
    @Transactional
    public BorrowRecord borrowBook(UUID borrowerId, UUID bookId) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + borrowerId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        long activeBorrowCount = recordRepository.findByBorrowerId(borrowerId).stream()
                .filter(r -> r.getReturnDate() == null)
                .count();

        if (activeBorrowCount >= borrower.getMaxBorrowLimit()) {
            throw new IllegalStateException("Borrow limit exceeded for borrower: " + borrower.getName());
        }

        if (book.getAvailableCopies() < 1) {
            throw new IllegalStateException("No available copies for book: " + book.getTitle());
        }

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setAvailable(book.getAvailableCopies() > 0);
        bookRepository.save(book);

        BorrowRecord record = BorrowRecord.builder()
                .book(book)
                .borrower(borrower)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .fineAmount(0.0) // Initialize fine
                .build();

        return recordRepository.save(record);
    }

    /**
     * Return a borrowed book and calculate fine if overdue
     */
    @Transactional
    public BorrowRecord returnBook(UUID borrowerId, UUID bookId) {
        BorrowRecord record = recordRepository.findByBorrowerId(borrowerId).stream()
                .filter(r -> r.getBook().getId().equals(bookId) && r.getReturnDate() == null)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record not found for book id: " + bookId));

        record.setReturnDate(LocalDate.now());

        // Fine calculation
        double fineAmount = 0.0;
        if (record.getReturnDate().isAfter(record.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), record.getReturnDate());
            double finePerDay = getFinePerDayForCategory(record.getBook().getCategory());
            fineAmount = daysLate * finePerDay;
        }
        record.setFineAmount(fineAmount);

        // Update book availability
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setAvailable(true);
        bookRepository.save(book);

        return recordRepository.save(record);
    }

    /**
     * Get fine per day for a book category
     */
    private double getFinePerDayForCategory(String category) {
        if (category == null) return DEFAULT_FINE_PER_DAY;

        return finePolicyRepository.findAll().stream()
                .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                .map(FinePolicy::getFinePerDay)
                .findFirst()
                .orElse(DEFAULT_FINE_PER_DAY);
    }

    /**
     * List all active borrow records
     */
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getActiveRecords() {
        return recordRepository.findByReturnDateIsNull()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get borrow history of a borrower
     */
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getBorrowHistory(UUID borrowerId) {
        return recordRepository.findByBorrowerId(borrowerId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * List overdue borrow records
     */
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getOverdueRecords() {
        LocalDate today = LocalDate.now();
        return recordRepository.findByReturnDateIsNullAndDueDateBefore(today)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert BorrowRecord entity to DTO
     */
    public BorrowRecordResponse toDto(BorrowRecord record) {
        return BorrowRecordResponse.builder()
                .id(record.getId())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .borrowerId(record.getBorrower().getId())
                .borrowerName(record.getBorrower().getName())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .fineAmount(record.getFineAmount() != null ? record.getFineAmount() : 0.0)
                .build();
    }
}
