import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { AuthService } from './auth.service';

export interface CommentRequest {
  postId: number;
  content: string;
  author: string;
}

export interface CommentResponse {
  id: number;
  postId: number;
  content: string;
  author: string;
}

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private baseUrl = `${environment.apiUrl}/comments`;
  private http: HttpClient = inject(HttpClient);
  authService: AuthService = inject(AuthService);

  private getHeaders(): HttpHeaders {
    const role = this.authService.getRole();
    return new HttpHeaders({
      Role: role || ''
    });
  }

  createComment(commentRequest: CommentRequest): Observable<CommentResponse> {
    return this.http.post<CommentResponse>(`${this.baseUrl}`, commentRequest, { headers: this.getHeaders() });
  }

  getCommentsForPost(postId: number): Observable<CommentResponse[]> {
    return this.http.get<CommentResponse[]>(`${this.baseUrl}/${postId}`, { headers: this.getHeaders() });
  }

  updateComment(commentId: number, commentRequest: CommentRequest): Observable<CommentResponse> {
    return this.http.patch<CommentResponse>(`${this.baseUrl}/${commentId}`, commentRequest, { headers: this.getHeaders() });
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${commentId}`, { headers: this.getHeaders() });
  }
}