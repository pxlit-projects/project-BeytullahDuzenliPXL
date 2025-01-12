import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommentService, CommentResponse, CommentRequest } from '../../services/comment.service';
import { PostService, PostResponse } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.css'
})
export class PostDetailComponent implements OnInit {
  post: PostResponse | null = null;
  comments: CommentResponse[] = [];
  newCommentContent: string = '';
  isEditing: { [key: number]: boolean } = {};
  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private commentService: CommentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const postId = this.route.snapshot.params['id'];
    if (postId) {
      this.loadPost(postId);
      this.loadComments(postId);
    }
  }

  getUser() {
    return this.authService.getUser();
  }

  loadPost(postId: number): void {
    this.postService.getPostById(postId).subscribe((post) => {
      this.post = post;
    });
  }

  loadComments(postId: number): void {
    this.commentService.getCommentsForPost(postId).subscribe((comments) => {
      this.comments = comments;
    });
  }

  addComment(): void {
    if (this.newCommentContent.trim()) {
      const commentRequest: CommentRequest = {
        postId: this.post?.id || 0,
        content: this.newCommentContent,
        author: this.authService.getUser() || '',
      };

      this.commentService.createComment(commentRequest).subscribe((comment) => {
        this.comments.push(comment);
        this.newCommentContent = '';
      });
    }
  }

  startEditing(commentId: number): void {
    this.isEditing[commentId] = true;
  }

  stopEditing(commentId: number): void {
    this.isEditing[commentId] = false;
  }

  updateComment(comment: CommentResponse): void {
    const updatedCommentRequest: CommentRequest = {
      postId: comment.postId,
      content: comment.content,
      author: comment.author,
    };

    this.commentService.updateComment(comment.id, updatedCommentRequest).subscribe(() => {
      this.stopEditing(comment.id);
    });
  }

  deleteComment(commentId: number): void {
    this.commentService.deleteComment(commentId).subscribe(() => {
      this.comments = this.comments.filter((comment) => comment.id !== commentId);
    });
  }
}
