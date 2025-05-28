package com.arifng.springmongodb.service;

import com.arifng.springmongodb.Author;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthorTemplateServiceImpl implements AuthorTemplateService {
    private final MongoTemplate mongoTemplate;

    public AuthorTemplateServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Author createAuthor(Author author) {
        return mongoTemplate.insert(author);
    }

    @Override
    public Author updateAuthor(Author author) {
        return mongoTemplate.save(author);
    }

    @Override
    public void deleteAuthor(Author author) {
        mongoTemplate.remove(author);
    }

    @Override
    public Author findById(UUID id) {
        return mongoTemplate.findById(id, Author.class);
    }

    @Override
    public List<Author> findByQueryCriteria(Query query) {
        return mongoTemplate.find(query, Author.class);
    }

    @Override
    public UpdateResult updateMultipleAuthors(Query query, Update update) {
        return mongoTemplate.updateMulti(query, update, Author.class);
    }

    @Override
    public void upsert(Query query, Update update) {
        mongoTemplate.upsert(query, update, Author.class);
    }
}
