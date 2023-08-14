package com.example.bootr2dbc.services;

import com.example.bootr2dbc.entities.ReactiveComments;
import com.example.bootr2dbc.model.ReactiveCommentRequest;
import com.example.bootr2dbc.repositories.ReactiveCommentsRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class ReactiveCommentsService {

    private final ReactiveCommentsRepository reactiveCommentsRepository;

    public Flux<ReactiveComments> findAllReactiveCommentsByPostId(Long postId, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return reactiveCommentsRepository.findAllByPostId(postId, sort);
    }

    public Mono<ReactiveComments> findReactiveCommentById(UUID id) {
        return reactiveCommentsRepository.findById(id);
    }

    public Mono<ReactiveComments> saveReactiveCommentByPostId(ReactiveCommentRequest reactiveCommentRequest) {
        ReactiveComments reactiveComments = mapToReactiveComments(reactiveCommentRequest);
        return reactiveCommentsRepository.save(reactiveComments);
    }

    public Mono<ReactiveComments> updateReactivePostComment(ReactiveCommentRequest reactiveCommentRequest, UUID id) {
        ReactiveComments reactiveComments = mapToReactiveComments(reactiveCommentRequest);
        reactiveComments.setId(id);
        return reactiveCommentsRepository.save(reactiveComments);
    }

    public Mono<Void> deleteReactiveCommentById(UUID id) {
        return reactiveCommentsRepository.deleteById(id);
    }

    private ReactiveComments mapToReactiveComments(ReactiveCommentRequest reactiveCommentRequest) {
        ReactiveComments reactiveComments = new ReactiveComments();
        reactiveComments.setContent(reactiveCommentRequest.content());
        reactiveComments.setTitle(reactiveCommentRequest.title());
        reactiveComments.setPostId(reactiveCommentRequest.postId());
        return reactiveComments;
    }
}
