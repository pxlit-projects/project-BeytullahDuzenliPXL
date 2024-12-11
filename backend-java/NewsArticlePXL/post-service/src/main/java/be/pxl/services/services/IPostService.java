package be.pxl.services.services;

import be.pxl.services.model.dto.PostRequest;
import be.pxl.services.model.dto.PostResponse;
import be.pxl.services.enums.Status;

import java.util.List;

public interface IPostService {
    PostResponse createPost(PostRequest postRequest);
    PostResponse updatePost(Long postId, PostRequest postRequest);
    List<PostResponse> getPostsByStatus(Status status);
    PostResponse getPostById(Long postId);
    PostResponse updateStatus(Long postId, Status status);
}