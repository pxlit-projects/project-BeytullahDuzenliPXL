import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { AuthService } from './auth.service';

export interface PostRequest {
  title: string;
  content: string;
  author: string;
  status: string;
}

export interface PostResponse {
  id: number;
  title: string;
  content: string;
  author: string;
  status: string;
  creationDate: string;
}

export interface NotificationResponse {
  message: string;
  author: string;
  postAuthor: string;
}

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private baseUrl = `${environment.apiUrl}/posts`;
  http: HttpClient = inject(HttpClient);
  authService: AuthService = inject(AuthService);

  private getHeaders(): HttpHeaders {
    const role = this.authService.getRole();
    return new HttpHeaders({
      Role: role || ''
    });
  }

  getAllPosts(): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${this.baseUrl}`, {headers: this.getHeaders()});
  }

  getFilteredPosts(content?: string, author?: string, fromDate?: string, toDate?: string, status?: string): Observable<PostResponse[]> {
    let params = new HttpParams();
    if (content) params = params.set('content', content);
    if (author) params = params.set('author', author);
    if (fromDate) params = params.set('fromDate', fromDate);
    if (toDate) params = params.set('toDate', toDate);
    if (status) params = params.set('status', status);

    return this.http.get<PostResponse[]>(`${this.baseUrl}`, { params, headers: this.getHeaders() });
  }

  getAllPostsByAuthor(author: string): Observable<PostResponse[]> {
    const params = new HttpParams().set('author', author);
    return this.http.get<PostResponse[]>(`${this.baseUrl}`, { params, headers: this.getHeaders() });
  }

  createPost(postRequest: PostRequest): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${this.baseUrl}`, postRequest, {headers: this.getHeaders()});
  }

  updatePost(postId: number, postRequest: PostRequest): Observable<PostResponse> {
    return this.http.put<PostResponse>(`${this.baseUrl}/${postId}`, postRequest, {headers: this.getHeaders()});
  }

  getPostById(postId: number): Observable<PostResponse> {
    return this.http.get<PostResponse>(`${this.baseUrl}/${postId}`, {headers: this.getHeaders()});
  }

  getPostsByStatus(status: string): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${this.baseUrl}/status/${status}`, {headers: this.getHeaders()});
  }

  updateStatus(postId: number, status: string): Observable<PostResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<PostResponse>(`${this.baseUrl}/${postId}/status`, {}, { params, headers: this.getHeaders() });
  }

  getNotificationsForAuthor(author: string): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(`${this.baseUrl}/notifications/${author}`, {headers: this.getHeaders()});
  }
}