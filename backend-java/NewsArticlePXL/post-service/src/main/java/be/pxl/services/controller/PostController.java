package be.pxl.services.controller;

import be.pxl.services.enums.Status;
import be.pxl.services.model.dto.PostRequest;
import be.pxl.services.model.dto.PostResponse;
import be.pxl.services.services.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.createPost(postRequest);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.updatePost(postId, postRequest);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/postId")
    public ResponseEntity<PostResponse> getPostById(@RequestParam Long postId) {
        PostResponse postResponse = postService.getPostById(postId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/status")
    public ResponseEntity<List<PostResponse>> getPostsByStatus(@RequestParam Status status) {
        List<PostResponse> posts = postService.getPostsByStatus(status);
        return ResponseEntity.ok(posts);
    }

    @PatchMapping("/{postId}/status")
    public ResponseEntity<PostResponse> updateStatus(@PathVariable Long postId, @RequestParam Status status) {
        PostResponse postResponse = postService.updateStatus(postId, status);
        return ResponseEntity.ok(postResponse);
    }
}