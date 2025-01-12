package be.pxl.services.repository;

import be.pxl.services.domain.Post;
import be.pxl.services.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(Status status);
    @Query("SELECT p FROM Post p " +
            "WHERE (:content IS NULL OR LOWER(p.content) LIKE LOWER(CONCAT('%', :content, '%'))) " +
            "AND (:author IS NULL OR LOWER(p.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:fromDate IS NULL OR p.creationDate >= :fromDate) " +
            "AND (:toDate IS NULL OR p.creationDate <= :toDate)" +
            "AND (:status IS NULL OR p.status = :status)")
    List<Post> findFilteredPosts(
            @Param("content") String content,
            @Param("author") String author,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("status") Status status
    );
}