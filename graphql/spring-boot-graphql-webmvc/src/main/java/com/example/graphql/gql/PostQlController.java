package com.example.graphql.gql;

import com.example.graphql.entities.PostEntity;
import com.example.graphql.model.request.NewPostRequest;
import com.example.graphql.projections.PostInfo;
import com.example.graphql.services.PostService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class PostQlController {

    private final PostService postService;

    @QueryMapping
    public List<PostInfo> allPostsByEmail(@Argument("email") String email) {
        return this.postService.findAllPostsByAuthorEmail(email);
    }

    @MutationMapping
    public PostEntity createPost(@Valid @Argument("newPostRequest") NewPostRequest newPostRequest) {
        return this.postService.createPost(newPostRequest);
    }
}
