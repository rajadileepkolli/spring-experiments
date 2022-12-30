package com.example.graphql.gql;

import com.example.graphql.dtos.PostInfo;
import com.example.graphql.entities.AuthorEntity;
import com.example.graphql.entities.PostCommentEntity;
import com.example.graphql.entities.TagEntity;
import com.example.graphql.services.AuthorService;
import com.example.graphql.services.PostCommentService;
import com.example.graphql.services.PostService;
import com.example.graphql.services.TagService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
@Slf4j
@RequiredArgsConstructor
public class AuthorGraphQlController {

    private final AuthorService authorService;

    private final PostService postService;

    private final PostCommentService postCommentService;

    private final TagService tagService;

    @BatchMapping(typeName = "Author")
    public Map<AuthorEntity, List<PostInfo>> posts(List<AuthorEntity> authors) {
        log.info("Fetching PostInformation by AuthodIds");
        List<Long> authorIds = authors.stream().map(AuthorEntity::getId).toList();

        var authorPostsMap = this.postService.getPostByAuthorIdIn(authorIds);

        return authors.stream()
                .collect(
                        Collectors.toMap(
                                author -> author,
                                author ->
                                        authorPostsMap.getOrDefault(
                                                author.getId(), new ArrayList<>())));
    }

    @BatchMapping(typeName = "Post")
    public Map<PostInfo, List<PostCommentEntity>> comments(List<PostInfo> posts) {
        log.info("Fetching PostComments by PostIds");
        List<Long> postIds = posts.stream().map(PostInfo::getId).toList();

        var postCommentsMap = this.postCommentService.getCommentsByPostIdIn(postIds);

        return posts.stream()
                .collect(
                        Collectors.toMap(
                                post -> post,
                                post ->
                                        postCommentsMap.getOrDefault(
                                                post.getId(), new ArrayList<>())));
    }

    @BatchMapping(typeName = "Post")
    public Map<PostInfo, List<TagEntity>> tags(List<PostInfo> posts) {
        log.info("Fetching Tags by PostIds");
        List<Long> postIds = posts.stream().map(PostInfo::getId).toList();

        var postCommentsMap = this.tagService.getTagsByPostIdIn(postIds);

        return posts.stream()
                .collect(
                        Collectors.toMap(
                                post -> post,
                                post ->
                                        postCommentsMap.getOrDefault(
                                                post.getId(), new ArrayList<>())));
    }

    @QueryMapping
    public List<AuthorEntity> allAuthors() {
        return this.authorService.findAllAuthors();
    }

    @QueryMapping
    public Optional<AuthorEntity> findAuthorByEmailId(@Argument("email") String email) {
        return this.authorService.findAuthorByEmailId(email);
    }
}
