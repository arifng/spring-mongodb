package com.arifng.springmongodb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection = "authors")
@Data
public class Author {
    @Id
    private UUID id;

    @Field(name = "full_name")
    private String name;

    @Indexed(unique = true)
    private String email;

    @Field(name = "articles_count")
    private Integer articlesCount;

    private Boolean active;
}
