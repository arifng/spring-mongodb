package com.arifng.springmongodb.service;

import com.arifng.springmongodb.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorService {
    Author createAuthor(Author author);

    Optional<Author> findById(UUID id);

    Boolean existById(UUID id);

    void deleteAuthor(Author author);

    void saveAll(List<Author> authors);

    Page<Author> findAll(PageRequest pageRequest);

    Optional<Author> findByEmail(String email);

    List<Author> findActiveAuthorsWithArticlesCountMoreOrEqualTo(int articlesCount);

    List<Author> findActiveAuthorsInArticlesCountRange(int minArticlesCount, int maxArticlesCount);

    List<Author> retrieveActiveEmails();

}
