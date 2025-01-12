import { Component, OnInit } from '@angular/core';
import { PostService, PostResponse } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-review-posts',
  standalone: true,
  imports: [],
  templateUrl: './review-posts.component.html',
  styleUrl: './review-posts.component.css'
})
export class ReviewPostsComponent implements OnInit {
  submittedPosts: PostResponse[] = [];

  constructor(private postService: PostService, 
    private authService: AuthService, 
    private router: Router) {}

  ngOnInit(): void {
    this.loadSubmittedPosts();
  }

  loadSubmittedPosts(): void {
    const user = this.authService.getUser();
    this.postService.getPostsByStatus('SUBMITTED').subscribe((posts) => {
      this.submittedPosts = posts.filter(post => post.author !== user);
    });
  }

  viewPostDetail(postId: number): void {
    this.router.navigate(['/review', postId]);
  }
}