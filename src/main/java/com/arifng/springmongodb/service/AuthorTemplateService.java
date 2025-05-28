package com.arifng.springmongodb.service;

import com.arifng.springmongodb.Author;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.UUID;

public interface AuthorTemplateService {
    Author createAuthor(Author author);

    Author updateAuthor(Author author);

    void deleteAuthor(Author author);

    Author findById(UUID id);

    List<Author> findByQueryCriteria(Query query);

    UpdateResult updateMultipleAuthors(Query query, Update update);

    void upsert(Query query, Update update);
}
