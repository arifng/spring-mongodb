package com.arifng.springmongodb;

import com.arifng.springmongodb.respository.AuthorRepository;
import com.arifng.springmongodb.service.AuthorService;
import com.arifng.springmongodb.service.AuthorServiceImpl;
import net.bytebuddy.utility.RandomString;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class AuthorServiceTest {
    @Autowired
    private AuthorRepository authorRepository;
    private AuthorService authorService;

    @BeforeEach
    void setup() {
        authorService = new AuthorServiceImpl(authorRepository);
    }

    @AfterEach
    void tearDown() {
        authorRepository.deleteAll();
    }

    @Test
    void saveDummyAuthor() {
        Author author = Instancio.create(Author.class);
        Author createdAuthor = authorService.createAuthor(author);

        String updatedName = RandomString.make();
        createdAuthor.setName(updatedName);
        authorService.createAuthor(createdAuthor);

        Optional<Author> updatedAuthor = authorService.findById(createdAuthor.getId());
        assertThat(updatedAuthor)
                .isPresent()
                .get()
                .extracting(Author::getName)
                .isEqualTo(updatedName);
    }

    @Test
    void deleteDummyAuthor() {
        Author author = Instancio.create(Author.class);
        Author createdAuthor = authorService.createAuthor(author);

        Boolean authorExists = authorService.existById(createdAuthor.getId());
        assertThat(authorExists).isTrue();

        authorService.deleteAuthor(createdAuthor);

        authorExists = authorService.existById(createdAuthor.getId());
        assertThat(authorExists).isFalse();
    }

    @Test
    void paginationAndSorting() {
        List<Author> authors = Instancio.ofList(Author.class)
                .size(10)
                .create();
        authorService.saveAll(authors);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        Page<Author> authorResult = authorService.findAll(pageRequest);

        assertThat(authorResult.getContent())
                .hasSize(5)
                .extracting(Author::getName)
                .isSorted();
    }

    @Test
    void findByEmail() {
        Author author = Instancio.create(Author.class);
        authorService.createAuthor(author);

        Optional<Author> byEmail = authorService.findByEmail(author.getEmail());

        assertThat(byEmail)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(author);
    }

    @Test
    void activeAuthorWithArticleCount() {
        int articlesCount = 10;
        Author author = Instancio.of(Author.class)
                .set(field(Author::getActive), true)
                .generate(field(Author::getArticlesCount), gen -> gen.ints().min(articlesCount))
                .create();
        authorService.createAuthor(author);

        List<Author> authors = authorService.findActiveAuthorsWithArticlesCountMoreOrEqualTo(5);
        assertThat(authors)
                .singleElement()
                .usingRecursiveComparison()
                .isEqualTo(author);
    }

    @Test
    void activeAuthorWithArticleCountRange() {
        int minArticlesCount = 5;
        int maxArticlesCount = 15;
        Author author = Instancio.of(Author.class)
                .set(field(Author::getActive), true)
                .generate(field(Author::getArticlesCount), gen -> gen.ints().range(minArticlesCount, maxArticlesCount))
                .create();
        authorService.createAuthor(author);

        List<Author> authors = authorService.findActiveAuthorsInArticlesCountRange(minArticlesCount, maxArticlesCount);
        assertThat(authors)
                .singleElement()
                .usingRecursiveComparison()
                .isEqualTo(author);
    }

    @Test
    void retrieveAuthorEmailOnly() {
        int authorsCount = 10;
        List<Author> authors = Instancio.ofList(Author.class)
                .size(authorsCount)
                .set(field(Author::getActive), true)
                .create();
        authorService.saveAll(authors);

        List<Author> activeEmails = authorService.retrieveActiveEmails();
        assertThat(activeEmails)
                .hasSize(authorsCount)
                .allSatisfy(author ->
                        assertThat(author)
                                .hasAllNullFieldsOrPropertiesExcept("email", "id")
                );
    }
}
