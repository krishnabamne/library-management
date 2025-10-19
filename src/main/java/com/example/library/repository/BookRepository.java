package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByTitleAndDeletedFalse(String title);
    Optional<Book> findByIsbnAndDeletedFalse(String isbn);
    List<Book> findByCategoryAndDeletedFalse(String category);
}
