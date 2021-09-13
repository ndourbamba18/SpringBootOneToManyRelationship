package com.parlonsdev.controller;

import com.parlonsdev.dto.CommentDto;
import com.parlonsdev.exception.ResourceNotFoundException;
import com.parlonsdev.message.ResponseMessage;
import com.parlonsdev.model.Comment;
import com.parlonsdev.repository.CommentRepository;
import com.parlonsdev.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/v1")
public class CommentController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentController(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping(path = "/posts/{postId}/comments")
    public ResponseEntity<Page<Comment>> getAllCommentsByPostId(@PathVariable("postId") Long postId,
                                                                    Pageable pageable ){
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        if (comments.isEmpty())
            return new ResponseEntity(new ResponseMessage("Comments empty!"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable (value = "postId") Long postId,
                                 @Valid @RequestBody CommentDto commentDto) {
        if (!postRepository.existsById(postId))
            throw new ResourceNotFoundException("PostId : " + postId + " not found");
        if (commentDto.getText().isBlank())
            return new ResponseEntity(new ResponseMessage("Text is required!"), HttpStatus.BAD_REQUEST);
        if (commentDto.getText().length()<3)
            return new ResponseEntity(new ResponseMessage("Ce champ doit contenir au moins 3 characters!"), HttpStatus.BAD_REQUEST);


        Comment comment = new Comment(commentDto.getText(), commentDto.getPost());
        postRepository.findById(postId).map(post -> {
            comment.setPost(post);
            return commentRepository.save(comment);
        });
        return new ResponseEntity(new ResponseMessage("Comment saved successfully!"), HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable (value = "postId") Long postId,
                                 @PathVariable (value = "commentId") Long commentId,
                                 @Valid @RequestBody CommentDto commentDto) {
        if(!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("PostId " + postId + " not found");
        }
        if (commentDto.getText().isBlank())
            return new ResponseEntity(new ResponseMessage("Text is required!"), HttpStatus.BAD_REQUEST);
        if (commentDto.getText().length()<3)
            return new ResponseEntity(new ResponseMessage("Ce champ doit contenir au moins 3 characters!"), HttpStatus.BAD_REQUEST);


        commentRepository.findById(commentId).map(comment -> {
            comment.setText(commentDto.getText());
            return commentRepository.save(comment);
        }).orElseThrow(() -> new ResourceNotFoundException("CommentId " + commentId + "not found"));
        return new ResponseEntity(new ResponseMessage("Comment updated successfully!"), HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable (value = "postId") Long postId,
                                           @PathVariable (value = "commentId") Long commentId) {
        return commentRepository.findByIdAndPostId(commentId, postId).map(comment -> {
            commentRepository.delete(comment);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId + " and postId " + postId));
    }

    @DeleteMapping("/posts/{postId}/comments")
    public ResponseEntity<?> deleteAllComments() {
            commentRepository.deleteAll();
            return ResponseEntity.ok().build();
    }
}
