package be.pxl.services.controller;

import be.pxl.services.domain.dto.Request.ReviewRequest;
import be.pxl.services.domain.dto.Response.ReviewResponse;
import be.pxl.services.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> makeReview(@RequestBody ReviewRequest reviewRequest, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ReviewResponse reviewResponse = reviewService.makeReviewForPost(reviewRequest, role);
        return ResponseEntity.ok(reviewResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long postId, @RequestHeader("Role") String role) {
        if (!role.equals("redacteur")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ReviewResponse reviewResponse = reviewService.getReviewForPost(postId, role);
        return ResponseEntity.ok(reviewResponse);
    }
}