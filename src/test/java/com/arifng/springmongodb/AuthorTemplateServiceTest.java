package com.arifng.springmongodb;

import com.arifng.springmongodb.service.AuthorTemplateService;
import com.arifng.springmongodb.service.AuthorTemplateServiceImpl;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.bulk.UpdateRequest;
import net.bytebuddy.utility.RandomString;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class AuthorTemplateServiceTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    private AuthorTemplateService authorTemplateService;

    @BeforeEach
    void setup() {
        authorTemplateService = new AuthorTemplateServiceImpl(mongoTemplate);
    }

    @Test
    void saveAndUpdateAuthor() {
        Author author = Instancio.create(Author.class);
        Author createdAuthor = authorTemplateService.createAuthor(author);

        Author retrievedAuthor = authorTemplateService.findById(createdAuthor.getId());

        assertThat(retrievedAuthor)
                .isNotNull()
                .extracting(Author::getId)
                .isEqualTo(createdAuthor.getId());

        String updatedName = RandomString.make();
        createdAuthor.setName(updatedName);
        Author updatedAuthor = authorTemplateService.updateAuthor(createdAuthor);
        retrievedAuthor = authorTemplateService.findById(createdAuthor.getId());
        assertThat(retrievedAuthor)
                .isNotNull()
                .extracting(Author::getName)
                .isEqualTo(updatedName);
    }

    @Test
    void deleteAuthor() {
        Author author = Instancio.create(Author.class);
        Author createdAuthor = authorTemplateService.createAuthor(author);

        authorTemplateService.deleteAuthor(createdAuthor);
        Author deletedAuthor = authorTemplateService.findById(createdAuthor.getId());
        assertThat(deletedAuthor).isNull();
    }

    @Test
    void activeAuthorWithSpecifiedDomainEmail() {
        Author author = Instancio.of(Author.class)
                .set(field(Author::getActive), true)
                .generate(field(Author::getEmail), gen -> gen.text().pattern("[a-zA-Z0-9._%+-]+@example.com"))
                .create();
        authorTemplateService.createAuthor(author);

        Criteria criteria = Criteria.where("active").is(true)
                .and("email").regex("@example\\.com$");
        Query query = new Query();
        query.addCriteria(criteria);

        List<Author> authorList = authorTemplateService.findByQueryCriteria(query);

        assertThat(authorList)
                .singleElement()
                .usingRecursiveComparison()
                .isEqualTo(author);
    }

    @Test
    void verifyModifiedCountOfUpdatedAuthor() {
        int lowArticlesCount = 10;
        int authorsCount = 20;

        for (int i = 0; i < authorsCount; i++) {
            Author author = Instancio.of(Author.class)
                    .set(field(Author::getActive), true)
                    .generate(field(Author::getArticlesCount), gen -> gen.ints().max(lowArticlesCount))
                    .create();
            authorTemplateService.createAuthor(author);
        }

        Query query = new Query(Criteria.where("articles_count").lte(lowArticlesCount));
        Update update = new Update().set("active", false);
        UpdateResult updateResult = authorTemplateService.updateMultipleAuthors(query, update);

        assertThat(updateResult.getModifiedCount())
                .isEqualTo(authorsCount);
    }

    @Test
    void verifyUpsert() {
        UUID authorId = UUID.randomUUID();
        String email = RandomString.make() + "@example.com";
        String name = RandomString.make();

        Query query = new Query(Criteria.where("email").is(email));
        Update update = new Update()
                .set("name", name)
                .setOnInsert("id", authorId)
                .setOnInsert("active", true);

        authorTemplateService.upsert(query, update);
        List<Author> upsertedAuthors = authorTemplateService.findByQueryCriteria(query);

        assertThat(upsertedAuthors)
                .singleElement()
                .isNotNull()
                .extracting(Author::getName)
                .isEqualTo(name);


        String updatedName = RandomString.make();
        update = new Update()
                .setOnInsert("name", updatedName);

        authorTemplateService.upsert(query, update);
        upsertedAuthors = authorTemplateService.findByQueryCriteria(query);

        assertThat(upsertedAuthors)
                .singleElement()
                .isNotNull()
                .extracting(Author::getName)
                .isEqualTo(name); // Name should not change since it was not set in the upsert
    }
}
