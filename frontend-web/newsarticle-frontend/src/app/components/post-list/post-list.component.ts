import { Component, OnInit } from '@angular/core';
import { PostService, PostResponse } from '../../services/post.service';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css']
})
export class PostListComponent implements OnInit {
  posts: PostResponse[] = [];
  filteredPosts: PostResponse[] = [];
  searchQuery: string = '';
  selectedAuthor: string = '';
  fromDate: string | null = null;
  toDate: string | null = null;

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.fetchPosts();
  }

  fetchPosts(): void {
    this.postService
      .getFilteredPosts(
      this.searchQuery, 
      this.selectedAuthor, 
      this.fromDate ? this.fromDate + "T00:00:00" : undefined, 
      this.toDate ? this.toDate + "T23:59:59" : undefined,
      'PUBLISHED'
      )
      .subscribe((data) => {
      this.posts = data;
      console.log("posts:::::", this.posts);
      this.posts.reverse();
      this.filteredPosts = data;
      });
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.selectedAuthor = '';
    this.fromDate = null;
    this.toDate = null;
    this.fetchPosts();
  }

  applyFilters(): void {
    this.fetchPosts();
  }
  updateToDateLimit(): void {
    if (this.fromDate && this.toDate && this.toDate < this.fromDate) {
      this.toDate = null;
    }
  }
}