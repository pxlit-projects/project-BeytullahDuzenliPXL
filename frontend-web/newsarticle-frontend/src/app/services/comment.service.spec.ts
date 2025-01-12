import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CommentService, CommentRequest, CommentResponse } from './comment.service';
import { environment } from '../../environments/environment.development';
import { AuthService } from './auth.service';
import { HttpErrorResponse } from '@angular/common/http';

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;
  const baseUrl = `${environment.apiUrl}/comments`;

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getRole']);
    authServiceSpy.getRole.and.returnValue('USER');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        CommentService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('createComment', () => {
    it('should create a comment successfully', () => {
      const commentRequest: CommentRequest = {
        postId: 1,
        content: 'Test comment',
        author: 'Author'
      };
      const mockResponse: CommentResponse = {
        id: 1,
        ...commentRequest
      };

      service.createComment(commentRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.content).toBe('Test comment');
      });

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(commentRequest);
      expect(req.request.headers.get('Role')).toBe('USER');
      req.flush(mockResponse);
    });

    it('should handle bad request error when creating comment', () => {
      const commentRequest: CommentRequest = {
        postId: 1,
        content: '',
        author: 'Author'
      };

      service.createComment(commentRequest).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(400);
          expect(error.statusText).toBe('Bad Request');
        }
      });

      const req = httpMock.expectOne(baseUrl);
      req.flush('Invalid comment', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle server error when creating comment', () => {
      const commentRequest: CommentRequest = {
        postId: 1,
        content: 'Test comment',
        author: 'Author'
      };

      service.createComment(commentRequest).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Internal Server Error');
        }
      });

      const req = httpMock.expectOne(baseUrl);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getCommentsForPost', () => {
    it('should retrieve comments for a specific post', () => {
      const postId = 1;
      const mockComments: CommentResponse[] = [
        { id: 1, postId: 1, content: 'First comment', author: 'Author1' },
        { id: 2, postId: 1, content: 'Second comment', author: 'Author2' }
      ];

      service.getCommentsForPost(postId).subscribe(comments => {
        expect(comments).toEqual(mockComments);
        expect(comments.length).toBe(2);
        expect(comments[0].content).toBe('First comment');
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      expect(req.request.method).toBe('GET');
      expect(req.request.headers.get('Role')).toBe('USER');
      req.flush(mockComments);
    });

    it('should handle empty comments array', () => {
      const postId = 1;
      const mockComments: CommentResponse[] = [];

      service.getCommentsForPost(postId).subscribe(comments => {
        expect(comments).toEqual([]);
        expect(comments.length).toBe(0);
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      req.flush(mockComments);
    });

    it('should handle not found error when retrieving comments', () => {
      const postId = 999;

      service.getCommentsForPost(postId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
          expect(error.statusText).toBe('Not Found');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      req.flush('Post not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('updateComment', () => {
    it('should update a comment successfully', () => {
      const commentId = 1;
      const commentRequest: CommentRequest = {
        postId: 1,
        content: 'Updated comment',
        author: 'Author'
      };
      const mockResponse: CommentResponse = {
        id: commentId,
        ...commentRequest
      };

      service.updateComment(commentId, commentRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.content).toBe('Updated comment');
      });

      const req = httpMock.expectOne(`${baseUrl}/${commentId}`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(commentRequest);
      req.flush(mockResponse);
    });

    it('should handle not found error when updating comment', () => {
      const commentId = 999;
      const commentRequest: CommentRequest = {
        postId: 1,
        content: 'Updated comment',
        author: 'Author'
      };

      service.updateComment(commentId, commentRequest).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
          expect(error.statusText).toBe('Not Found');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${commentId}`);
      req.flush('Comment not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('deleteComment', () => {
    it('should delete a comment successfully', () => {
      const commentId = 1;

      service.deleteComment(commentId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${baseUrl}/${commentId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle not found error when deleting comment', () => {
      const commentId = 999;

      service.deleteComment(commentId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
          expect(error.statusText).toBe('Not Found');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${commentId}`);
      req.flush('Comment not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle unauthorized error when deleting comment', () => {
      const commentId = 1;

      service.deleteComment(commentId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(403);
          expect(error.statusText).toBe('Forbidden');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${commentId}`);
      req.flush('Unauthorized', { status: 403, statusText: 'Forbidden' });
    });
  });

  describe('Headers Functionality', () => {
    it('should use correct headers from AuthService with ADMIN role', () => {
      authService.getRole.and.returnValue('ADMIN');

      service.getCommentsForPost(1).subscribe();

      const req = httpMock.expectOne(`${baseUrl}/1`);
      expect(req.request.headers.get('Role')).toBe('ADMIN');
    });

    it('should handle missing role gracefully', () => {
      authService.getRole.and.returnValue(null);

      service.getCommentsForPost(1).subscribe();

      const req = httpMock.expectOne(`${baseUrl}/1`);
      expect(req.request.headers.get('Role')).toBe('');
    });
  });
});
