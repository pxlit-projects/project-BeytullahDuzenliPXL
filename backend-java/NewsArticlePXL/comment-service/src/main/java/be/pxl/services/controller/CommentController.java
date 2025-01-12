package be.pxl.services.controller;

import be.pxl.services.domain.dto.request.CommentRequest;
import be.pxl.services.domain.dto.response.CommentResponse;
import be.pxl.services.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur") && !role.equals("gebruiker")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        CommentResponse commentResponse = commentService.createComment(commentRequest);
        return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForPost(@PathVariable Long postId, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur") && !role.equals("gebruiker")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<CommentResponse> comments = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur") && !role.equals("gebruiker")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        CommentResponse commentResponse = commentService.updateComment(commentId, commentRequest);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur") && !role.equals("gebruiker")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}