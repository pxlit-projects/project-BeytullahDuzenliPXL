import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReviewService, ReviewRequest, ReviewResponse } from './review.service';
import { environment } from '../../environments/environment.development';
import { AuthService } from './auth.service';
import { HttpErrorResponse } from '@angular/common/http';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;
  const baseUrl = `${environment.apiUrl}/reviews`;

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getRole']);
    authServiceSpy.getRole.and.returnValue('USER');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ReviewService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('makeReview', () => {
    it('should create a review successfully with ACCEPTED status', () => {
      const reviewRequest: ReviewRequest = {
        postId: 1,
        reason: 'Reason for review',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'ACCEPTED'
      };
      const mockResponse: ReviewResponse = { ...reviewRequest };

      service.makeReview(reviewRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.status).toBe('ACCEPTED');
      });

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(reviewRequest);
      expect(req.request.headers.get('Role')).toBe('USER');
      req.flush(mockResponse);
    });

    it('should create a review successfully with REJECTED status', () => {
      const reviewRequest: ReviewRequest = {
        postId: 1,
        reason: 'Content violation',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'REJECTED'
      };
      const mockResponse: ReviewResponse = { ...reviewRequest };

      service.makeReview(reviewRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.status).toBe('REJECTED');
      });

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);
    });

    it('should handle errors when making a review', () => {
      const reviewRequest: ReviewRequest = {
        postId: 1,
        reason: 'Reason for review',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'ACCEPTED'
      };

      service.makeReview(reviewRequest).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(400);
          expect(error.statusText).toBe('Bad Request');
        }
      });

      const req = httpMock.expectOne(baseUrl);
      req.flush('Invalid request', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle unexpected server errors', () => {
      const reviewRequest: ReviewRequest = {
        postId: 1,
        reason: 'Reason for review',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'ACCEPTED'
      };

      service.makeReview(reviewRequest).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Internal Server Error');
        }
      });

      const req = httpMock.expectOne(baseUrl);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getReviewForPost', () => {
    it('should retrieve a review for a specific post', () => {
      const postId = 1;
      const mockReview: ReviewResponse = {
        postId,
        reason: 'Reason for review',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'ACCEPTED'
      };

      service.getReviewForPost(postId).subscribe(review => {
        expect(review).toEqual(mockReview);
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      expect(req.request.method).toBe('GET');
      expect(req.request.headers.get('Role')).toBe('USER');
      req.flush(mockReview);
    });

    it('should retrieve a rejected review for a specific post', () => {
      const postId = 1;
      const mockReview: ReviewResponse = {
        postId,
        reason: 'Content violation',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'REJECTED'
      };

      service.getReviewForPost(postId).subscribe(review => {
        expect(review).toEqual(mockReview);
        expect(review.status).toBe('REJECTED');
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockReview);
    });

    it('should handle not found error when retrieving a review', () => {
      const postId = 999;

      service.getReviewForPost(postId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
          expect(error.statusText).toBe('Not Found');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      req.flush('Review not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle unauthorized access error', () => {
      const postId = 1;

      service.getReviewForPost(postId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(403);
          expect(error.statusText).toBe('Forbidden');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      req.flush('Forbidden access', { status: 403, statusText: 'Forbidden' });
    });

    it('should handle server errors while retrieving review', () => {
      const postId = 1;

      service.getReviewForPost(postId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Internal Server Error');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('Headers Functionality', () => {
    it('should use correct headers from AuthService with ADMIN role', () => {
      authService.getRole.and.returnValue('ADMIN');

      const reviewRequest: ReviewRequest = {
        postId: 1,
        reason: 'Reason for review',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'ACCEPTED'
      };

      service.makeReview(reviewRequest).subscribe();

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.headers.get('Role')).toBe('ADMIN');
    });

    it('should use correct headers from AuthService with USER role', () => {
      authService.getRole.and.returnValue('USER');

      service.getReviewForPost(1).subscribe();

      const req = httpMock.expectOne(`${baseUrl}/1`);
      expect(req.request.headers.get('Role')).toBe('USER');
    });

    it('should handle missing role gracefully', () => {
      authService.getRole.and.returnValue(null);

      const reviewRequest: ReviewRequest = {
        postId: 1,
        reason: 'Reason for review',
        postAuthor: 'PostAuthor',
        author: 'Author',
        status: 'ACCEPTED'
      };

      service.makeReview(reviewRequest).subscribe();

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.headers.get('Role')).toBe('');
    });

    it('should handle empty role string', () => {
      authService.getRole.and.returnValue('');

      service.getReviewForPost(1).subscribe();

      const req = httpMock.expectOne(`${baseUrl}/1`);
      expect(req.request.headers.get('Role')).toBe('');
    });
  });
});