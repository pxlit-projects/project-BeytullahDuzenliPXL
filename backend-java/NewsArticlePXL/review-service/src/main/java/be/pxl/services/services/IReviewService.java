package be.pxl.services.services;

import be.pxl.services.domain.dto.Request.ReviewRequest;
import be.pxl.services.domain.dto.Response.ReviewResponse;
import be.pxl.services.enums.Status;

public interface IReviewService {
    ReviewResponse getReviewForPost(Long postId, String role);
    ReviewResponse makeReviewForPost(ReviewRequest reviewRequest, String role);
}