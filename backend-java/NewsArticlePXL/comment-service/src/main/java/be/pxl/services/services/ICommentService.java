package be.pxl.services.services;

import be.pxl.services.domain.dto.request.CommentRequest;
import be.pxl.services.domain.dto.response.CommentResponse;

import java.util.List;

public interface ICommentService {
    CommentResponse createComment(CommentRequest commentRequest);
    List<CommentResponse> getCommentsForPost(Long postId);
    CommentResponse updateComment(Long id, CommentRequest commentRequest);
    void deleteComment(Long id);
}