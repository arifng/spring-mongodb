package com.arifng.springmongodb.respository;

import com.arifng.springmongodb.Author;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends MongoRepository<Author, UUID> {
    Optional<Author> findByEmail(String email);

    List<Author> findByActiveTrueAndArticlesCountGreaterThanEqual(int articlesCount);

    @Query("{ 'active': true, 'articlesCount': { $gte: ?0, $lte: ?1 } }")
    List<Author> findActiveAuthorInArticlesCountRange(int minArticlesCount, int maxArticlesCount);

    @Query(value = "{ 'active': true }", fields = "{ 'email': 1 }")
    List<Author> retrieveActiveEmails();

}
