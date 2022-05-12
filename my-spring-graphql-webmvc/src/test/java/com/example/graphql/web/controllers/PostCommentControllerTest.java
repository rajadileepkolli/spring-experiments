package com.example.graphql.web.controllers;

import static com.example.graphql.utils.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.graphql.entities.PostComment;
import com.example.graphql.services.PostCommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@WebMvcTest(controllers = PostCommentController.class)
@ActiveProfiles(PROFILE_TEST)
class PostCommentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostCommentService postCommentService;

    @Autowired private ObjectMapper objectMapper;

    private List<PostComment> postCommentList;

    @BeforeEach
    void setUp() {
        this.postCommentList = new ArrayList<>();
        this.postCommentList.add(new PostComment(1L, "text 1"));
        this.postCommentList.add(new PostComment(2L, "text 2"));
        this.postCommentList.add(new PostComment(3L, "text 3"));

        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new ConstraintViolationProblemModule());
    }

    @Test
    void shouldFetchAllPostComments() throws Exception {
        given(postCommentService.findAllPostComments()).willReturn(this.postCommentList);

        this.mockMvc
                .perform(get("/api/postcomments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(postCommentList.size())));
    }

    @Test
    void shouldFindPostCommentById() throws Exception {
        Long postCommentId = 1L;
        PostComment postComment = new PostComment(postCommentId, "text 1");
        given(postCommentService.findPostCommentById(postCommentId))
                .willReturn(Optional.of(postComment));

        this.mockMvc
                .perform(get("/api/postcomments/{id}", postCommentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(postComment.getText())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingPostComment() throws Exception {
        Long postCommentId = 1L;
        given(postCommentService.findPostCommentById(postCommentId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/postcomments/{id}", postCommentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostComment() throws Exception {
        given(postCommentService.savePostComment(any(PostComment.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        PostComment postComment = new PostComment(1L, "some text");
        this.mockMvc
                .perform(
                        post("/api/postcomments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(postComment.getText())));
    }

    @Test
    void shouldReturn400WhenCreateNewPostCommentWithoutText() throws Exception {
        PostComment postComment = new PostComment(null, null);

        this.mockMvc
                .perform(
                        post("/api/postcomments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postComment)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(
                        jsonPath(
                                "$.type",
                                is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdatePostComment() throws Exception {
        Long postCommentId = 1L;
        PostComment postComment = new PostComment(postCommentId, "Updated text");
        given(postCommentService.findPostCommentById(postCommentId))
                .willReturn(Optional.of(postComment));
        given(postCommentService.savePostComment(any(PostComment.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/postcomments/{id}", postComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(postComment.getText())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingPostComment() throws Exception {
        Long postCommentId = 1L;
        given(postCommentService.findPostCommentById(postCommentId)).willReturn(Optional.empty());
        PostComment postComment = new PostComment(postCommentId, "Updated text");

        this.mockMvc
                .perform(
                        put("/api/postcomments/{id}", postCommentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postComment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePostComment() throws Exception {
        Long postCommentId = 1L;
        PostComment postComment = new PostComment(postCommentId, "Some text");
        given(postCommentService.findPostCommentById(postCommentId))
                .willReturn(Optional.of(postComment));
        doNothing().when(postCommentService).deletePostCommentById(postComment.getId());

        this.mockMvc
                .perform(delete("/api/postcomments/{id}", postComment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(postComment.getText())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingPostComment() throws Exception {
        Long postCommentId = 1L;
        given(postCommentService.findPostCommentById(postCommentId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/postcomments/{id}", postCommentId))
                .andExpect(status().isNotFound());
    }
}
