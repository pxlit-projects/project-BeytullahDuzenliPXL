package be.pxl.services.repository;

import be.pxl.services.domain.Post;
import be.pxl.services.enums.Category;
import be.pxl.services.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(Status status);
}