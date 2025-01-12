import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService, PostRequest, PostResponse } from '../../services/post.service';
import { ReviewService, ReviewResponse } from '../../services/review.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-post-editor',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './post-editor.component.html',
  styleUrl: './post-editor.component.css',
})
export class PostEditorComponent implements OnInit {
  postId: number | null = null;
  postRequest: PostRequest = {
    title: '',
    content: '',
    author: '',
    status: 'DRAFT',
  };
  reviewReason: string | null = null;
  isEditMode = false;

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private reviewService: ReviewService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.postId = this.route.snapshot.params['id'];
    if (this.postId) {
      this.isEditMode = true;
      this.loadPost();
    }
  }

  loadPost(): void {
    if (this.postId) {
      this.postService.getPostById(this.postId).subscribe((post: PostResponse) => {
        this.postRequest = {
          title: post.title,
          content: post.content,
          author: post.author,
          status: post.status,
        };

        if (post.status === 'REJECTED') {
          if (this.postId !== null) {
            this.loadReview(this.postId);
          }
        }
      });
    }
  }

  private loadReview(postId: number): void {
    this.reviewService.getReviewForPost(postId).subscribe((review: ReviewResponse) => {
      this.reviewReason = review.reason;
    });
  }

  saveAsDraft(): void {
    this.postRequest.status = 'DRAFT';
    this.savePost();
  }

  submitForReview(): void {
    this.postRequest.status = 'SUBMITTED';
    this.savePost();
  }

  public savePost(): void {
    this.postRequest.author = this.authService.getUser() || '';

    if (this.isEditMode && this.postId) {
      this.postService.updatePost(this.postId, this.postRequest).subscribe(() => {
        this.router.navigate(['/myposts']);
      });
    } else {
      this.postService.createPost(this.postRequest).subscribe(() => {
        this.router.navigate(['/myposts']);
      });
    }
  }
}