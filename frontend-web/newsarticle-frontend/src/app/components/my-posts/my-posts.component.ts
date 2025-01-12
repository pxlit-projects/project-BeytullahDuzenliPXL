import { Component, OnInit } from '@angular/core';
import { PostService, PostRequest, PostResponse } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-my-posts',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './my-posts.component.html',
  styleUrl: './my-posts.component.css'
})
export class MyPostsComponent implements OnInit {
  myPosts: PostResponse[] = [];

  constructor(private postService: PostService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const author = this.authService.getUser();
    if (author) {
      this.postService.getAllPostsByAuthor(author).subscribe(post => {
        this.myPosts = post;
        this.myPosts.reverse();
      })
    }
  }

  createNewPost(): void {
    this.router.navigate(['/myposts/create']);
  }

  editPost(postId: number): void {
    this.router.navigate(['/myposts/edit', postId]);
  }
}
