package be.pxl.services.services;

import be.pxl.services.domain.dto.request.NotificationRequest;
import be.pxl.services.domain.dto.request.PostRequest;
import be.pxl.services.domain.dto.response.NotificationResponse;
import be.pxl.services.domain.dto.response.PostResponse;
import be.pxl.services.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface IPostService {
    PostResponse createPost(PostRequest postRequest);
    PostResponse updatePost(Long postId, PostRequest postRequest);
    List<PostResponse> getPostsByStatus(Status status);
    PostResponse getPostById(Long postId);
    PostResponse updateStatus(Long postId, Status status);
    List<PostResponse> getAllPosts(String content, String author, LocalDateTime fromDate, LocalDateTime toDate, Status status);
    void getNotification(NotificationRequest notificationRequest);
    List<NotificationResponse> getNotificationsForAuthor(String author);

}