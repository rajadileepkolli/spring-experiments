package com.example.graphql.web.controllers;

import static com.example.graphql.utils.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.graphql.entities.PostCommentEntity;
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

@WebMvcTest(controllers = PostCommentController.class)
@ActiveProfiles(PROFILE_TEST)
class PostCommentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostCommentService postCommentService;

    @Autowired private ObjectMapper objectMapper;

    private List<PostCommentEntity> postCommentList;

    @BeforeEach
    void setUp() {
        this.postCommentList = new ArrayList<>();
        this.postCommentList.add(
                PostCommentEntity.builder().id(1L).title("First PostComment").build());
        this.postCommentList.add(
                PostCommentEntity.builder().id(2L).title("Second PostComment").build());
        this.postCommentList.add(
                PostCommentEntity.builder().id(3L).title("Third PostComment").build());
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
        PostCommentEntity postComment =
                PostCommentEntity.builder().id(postCommentId).title("First PostComment").build();
        given(postCommentService.findPostCommentById(postCommentId))
                .willReturn(Optional.of(postComment));

        this.mockMvc
                .perform(get("/api/postcomments/{id}", postCommentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(postComment.getTitle())));
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
        given(postCommentService.savePostComment(any(PostCommentEntity.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        PostCommentEntity postComment =
                PostCommentEntity.builder().id(1L).title("First PostComment").build();
        this.mockMvc
                .perform(
                        post("/api/postcomments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is(postComment.getTitle())));
    }

    @Test
    void shouldUpdatePostComment() throws Exception {
        Long postCommentId = 1L;
        PostCommentEntity postComment =
                PostCommentEntity.builder().id(postCommentId).title("Updated PostComment").build();
        given(postCommentService.findPostCommentById(postCommentId))
                .willReturn(Optional.of(postComment));
        given(postCommentService.savePostComment(any(PostCommentEntity.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/postcomments/{id}", postComment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(postComment.getTitle())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingPostComment() throws Exception {
        Long postCommentId = 1L;
        given(postCommentService.findPostCommentById(postCommentId)).willReturn(Optional.empty());
        PostCommentEntity postComment =
                PostCommentEntity.builder().id(postCommentId).title("Updated PostComment").build();

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
        PostCommentEntity postComment =
                PostCommentEntity.builder().id(postCommentId).title("First PostComment").build();
        given(postCommentService.findPostCommentById(postCommentId))
                .willReturn(Optional.of(postComment));
        doNothing().when(postCommentService).deletePostCommentById(postComment.getId());

        this.mockMvc
                .perform(delete("/api/postcomments/{id}", postComment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(postComment.getTitle())));
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
