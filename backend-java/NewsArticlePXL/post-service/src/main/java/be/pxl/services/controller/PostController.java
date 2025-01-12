package be.pxl.services.controller;

import be.pxl.services.domain.Notification;
import be.pxl.services.domain.dto.request.NotificationRequest;
import be.pxl.services.domain.dto.response.NotificationResponse;
import be.pxl.services.enums.Status;
import be.pxl.services.domain.dto.request.PostRequest;
import be.pxl.services.domain.dto.response.PostResponse;
import be.pxl.services.services.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;

//    @GetMapping
//    public ResponseEntity<List<PostResponse>> getAllPosts() {
//        List<PostResponse> posts = postService.getAllPosts();
//        return ResponseEntity.ok(posts);
//    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PostResponse postResponse = postService.createPost(postRequest);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostRequest postRequest, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PostResponse postResponse = postService.updatePost(postId, postRequest);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur") && !role.equals("gebruiker")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PostResponse postResponse = postService.getPostById(postId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PostResponse>> getPostsByStatus(@PathVariable Status status, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<PostResponse> posts = postService.getPostsByStatus(status);
        return ResponseEntity.ok(posts);
    }

    @PatchMapping("/{postId}/status")
    public ResponseEntity<PostResponse> updateStatus(@PathVariable Long postId, @RequestParam Status status, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PostResponse postResponse = postService.updateStatus(postId, status);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping()
    public ResponseEntity<List<PostResponse>> getFilteredPosts(@RequestParam(required = false) String content,
                                                               @RequestParam(required = false) String author,
                                                               @RequestParam(required = false) LocalDateTime fromDate,
                                                               @RequestParam(required = false) LocalDateTime toDate,
                                                               @RequestParam(required = false) Status status,
                                                               @RequestHeader("Role") String role) {
        if (!role.equals("redacteur") && !role.equals("gebruiker")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<PostResponse> posts = postService.getAllPosts(content, author, fromDate, toDate, status);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/notification")
    public ResponseEntity<Void> getNotification(@RequestBody NotificationRequest notificationRequest) {
        postService.getNotification(notificationRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications/{author}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsForAuthor(@PathVariable String author, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<NotificationResponse> notifications = postService.getNotificationsForAuthor(author);
        return ResponseEntity.ok(notifications);
    }
}