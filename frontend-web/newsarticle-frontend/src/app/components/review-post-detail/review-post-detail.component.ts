import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService, PostResponse } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { ReviewService, ReviewRequest } from '../../services/review.service';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-review-post-detail',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './review-post-detail.component.html',
  styleUrl: './review-post-detail.component.css'
})
export class ReviewPostDetailComponent implements OnInit {
  post: PostResponse | null = null;
  reviewReason: string = '';

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private reviewService: ReviewService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const postId = this.route.snapshot.params['id'];
    if (postId) {
      this.loadPost(postId);
    }
  }

  loadPost(postId: number): void {
    this.postService.getPostById(postId).subscribe((post) => {
      this.post = post;
    });
  }

  submitReview(status: 'ACCEPTED' | 'REJECTED'): void {
    if (!this.post) return;

    const reviewRequest: ReviewRequest = {
      postId: this.post.id,
      reason: this.reviewReason,
      postAuthor: this.post.author,
      author: this.authService.getUser() || '',
      status,
    };

    this.reviewService.makeReview(reviewRequest).subscribe(() => {
      this.router.navigate(['/review']);
    });
  }
}
