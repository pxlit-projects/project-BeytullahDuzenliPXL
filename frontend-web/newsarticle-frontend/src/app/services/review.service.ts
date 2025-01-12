import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { AuthService } from './auth.service';

export interface ReviewRequest {
  postId: number;
  reason: string;
  postAuthor: string;
  author: string;
  status: string;
}

export interface ReviewResponse {
  postId: number;
  reason: string;
  postAuthor: string;
  author: string;
  status: string;
}

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  private baseUrl = `${environment.apiUrl}/reviews`;
  private http: HttpClient = inject(HttpClient);
  authService: AuthService = inject(AuthService);

  private getHeaders(): HttpHeaders {
    const role = this.authService.getRole();
    return new HttpHeaders({
      Role: role || ''
    });
  }

  makeReview(reviewRequest: ReviewRequest): Observable<ReviewResponse> {
    return this.http.post<ReviewResponse>(`${this.baseUrl}`, reviewRequest, { headers: this.getHeaders() });
  }

  getReviewForPost(postId: number): Observable<ReviewResponse> {
    return this.http.get<ReviewResponse>(`${this.baseUrl}/${postId}`, { headers: this.getHeaders() });
  }
}