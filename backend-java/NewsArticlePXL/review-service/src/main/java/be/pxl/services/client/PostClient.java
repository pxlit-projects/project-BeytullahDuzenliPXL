package be.pxl.services.client;

import be.pxl.services.domain.dto.Request.NotificationRequest;
import be.pxl.services.domain.dto.Response.PostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "post-service")
public interface PostClient {
    @PostMapping("/posts/notification")
    void sendNotification(@RequestBody NotificationRequest notificationRequest);

    @GetMapping("/posts/{postId}")
    PostResponse getPostById(@PathVariable("postId") Long postId, @RequestHeader("Role") String role);
}