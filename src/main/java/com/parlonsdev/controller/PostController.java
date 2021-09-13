package com.parlonsdev.controller;

import com.parlonsdev.dto.PostDto;
import com.parlonsdev.exception.ResourceNotFoundException;
import com.parlonsdev.message.ResponseMessage;
import com.parlonsdev.model.Post;
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
public class PostController {

    private final PostRepository postRepository;

    @Autowired
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        if (posts.isEmpty())
            return new ResponseEntity(new ResponseMessage("List of post is empty!"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable("postId") Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow( () -> new ResourceNotFoundException("PostId : " + postId + " not found"));
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/posts/{title}")
    public ResponseEntity<Post> getPostById(@PathVariable("title") String title){
        Post post = postRepository.findByTitle(title)
                .orElseThrow( () -> new ResourceNotFoundException("Post Title : " + title + " not found"));
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDto postDto) {
        if (postRepository.existsByTitle(postDto.getTitle()))
            return new ResponseEntity<>(new ResponseMessage("Title of the post exist!"), HttpStatus.BAD_REQUEST);
        if (postDto.getTitle().isBlank())
            return new ResponseEntity<>(new ResponseMessage("Title is required!"), HttpStatus.BAD_REQUEST);
        if (postDto.getContent().isBlank())
            return new ResponseEntity<>(new ResponseMessage("Content is required!"), HttpStatus.BAD_REQUEST);
        if (postDto.getTitle().length()<3)
            return new ResponseEntity<>(new ResponseMessage("Ce champ doit contenir au moins 3 character!"), HttpStatus.BAD_REQUEST);

        Post post = new Post(postDto.getTitle(), postDto.getContent());
        postRepository.save(post);
        return new ResponseEntity<>(new ResponseMessage("Post is created successfully!"), HttpStatus.CREATED);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable("postId") Long postId, @Valid @RequestBody PostDto postDto) {
        if(!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("PostId : " + postId + " not found");
        }
        if (postRepository.existsByTitle(postDto.getTitle()))
            return new ResponseEntity(new ResponseMessage("Title of the post exist!"), HttpStatus.BAD_REQUEST);
        if (postDto.getTitle().isBlank())
            return new ResponseEntity(new ResponseMessage("Title is required!"), HttpStatus.BAD_REQUEST);
        if (postDto.getContent().isBlank())
            return new ResponseEntity(new ResponseMessage("Content is required!"), HttpStatus.BAD_REQUEST);
        if (postDto.getTitle().length()<3)
            return new ResponseEntity(new ResponseMessage("Ce champ doit contenir au moins 3 character!"), HttpStatus.BAD_REQUEST);

        postRepository.findById(postId).map(post -> {
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            return postRepository.save(post);
        });
        return new ResponseEntity(new ResponseMessage("The post ("+postDto.getTitle()+") is updated successfully!"), HttpStatus.OK);
    }


    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        return postRepository.findById(postId).map(post -> {
            postRepository.delete(post);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }

    @DeleteMapping("/posts")
    public ResponseEntity<HttpStatus> deleteAllPosts(){
        postRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
