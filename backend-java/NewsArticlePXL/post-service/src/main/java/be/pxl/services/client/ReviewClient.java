package be.pxl.services.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "review-service")
public interface ReviewClient {
}