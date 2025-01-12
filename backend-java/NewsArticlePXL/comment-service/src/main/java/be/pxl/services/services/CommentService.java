package be.pxl.services.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.request.CommentRequest;
import be.pxl.services.domain.dto.response.CommentResponse;
import be.pxl.services.repository.CommentRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Override
    public CommentResponse createComment(CommentRequest commentRequest) {
        logger.info("Attempting to create a new comment for post with ID: {}", commentRequest.getPostId());
        Comment comment = Comment.builder()
            .content(commentRequest.getContent())
            .postId(commentRequest.getPostId())
            .author(commentRequest.getAuthor())
            .build();

        commentRepository.save(comment);
        logger.info("Comment with ID: {} created successfully", comment.getId());
        return mapToCommentResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsForPost(Long postId) {
        logger.info("Fetching comments for post with ID: {}", postId);
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
            .map(this::mapToCommentResponse)
            .toList();
    }

    @Override
    public CommentResponse updateComment(Long id, CommentRequest commentRequest) {
        logger.info("Attempting to update comment with ID: {}", id);
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Comment with ID: {} not found", id);
                return new NotFoundException("Comment not found");
            });

        comment.setContent(commentRequest.getContent());
        commentRepository.save(comment);
        logger.info("Comment with ID: {} updated successfully", id);
        return mapToCommentResponse(comment);
    }

    @Override
    public void deleteComment(Long id) {
        logger.info("Attempting to delete comment with ID: {}", id);
        commentRepository.deleteById(id);
        logger.info("Comment with ID: {} deleted successfully", id);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            comment.getPostId(),
            comment.getAuthor()
        );
    }
}