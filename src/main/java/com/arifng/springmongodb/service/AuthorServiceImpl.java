package com.arifng.springmongodb.service;

import com.arifng.springmongodb.Author;
import com.arifng.springmongodb.respository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Optional<Author> findById(UUID id) {
        return authorRepository.findById(id);
    }

    @Override
    public Boolean existById(UUID id) {
        return authorRepository.existsById(id);
    }

    @Override
    public void deleteAuthor(Author author) {
        authorRepository.delete(author);
    }

    @Override
    public void saveAll(List<Author> authors) {
        authorRepository.saveAll(authors);
    }

    @Override
    public Page<Author> findAll(PageRequest pageRequest) {
        return authorRepository.findAll(pageRequest);
    }

    @Override
    public Optional<Author> findByEmail(String email) {
        return authorRepository.findByEmail(email);
    }

    @Override
    public List<Author> findActiveAuthorsWithArticlesCountMoreOrEqualTo(int articlesCount) {
        return authorRepository.findByActiveTrueAndArticlesCountGreaterThanEqual(articlesCount);
    }

    @Override
    public List<Author> findActiveAuthorsInArticlesCountRange(int minArticlesCount, int maxArticlesCount) {
        return authorRepository.findActiveAuthorInArticlesCountRange(minArticlesCount, maxArticlesCount);
    }

    @Override
    public List<Author> retrieveActiveEmails() {
        return authorRepository.retrieveActiveEmails();
    }

    // Implement methods from AuthorService interface here
    // For example, you might have methods like:
    // - createAuthor(Author author)
    // - getAuthorById(UUID id)
    // - updateAuthor(UUID id, Author author)
    // - deleteAuthor(UUID id)

    // You can also inject the AuthorRepository here to interact with the database
    // private final AuthorRepository authorRepository;

    // public AuthorServiceImpl(AuthorRepository authorRepository) {
    //     this.authorRepository = authorRepository;
    // }
}
