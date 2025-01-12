import { TestBed } from '@angular/core/testing';
import { PostService, PostRequest, PostResponse } from './post.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment.development';
import { HttpErrorResponse } from '@angular/common/http';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;
  const baseUrl = `${environment.apiUrl}/posts`;

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getRole']);
    authServiceSpy.getRole.and.returnValue('USER');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PostService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllPosts', () => {
    it('should retrieve all posts with proper headers', () => {
      const mockPosts: PostResponse[] = [
        { id: 1, title: 'Test Post', content: 'Content', author: 'Author', status: 'PUBLISHED', creationDate: '2024-01-01' }
      ];

      service.getAllPosts().subscribe(posts => {
        expect(posts).toEqual(mockPosts);
      });

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.method).toBe('GET');
      expect(req.request.headers.get('Role')).toBe('USER');
      req.flush(mockPosts);
    });

    it('should handle error when API fails', () => {
      service.getAllPosts().subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Internal Server Error');
        }
      });

      const req = httpMock.expectOne(baseUrl);
      req.flush('Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getFilteredPosts', () => {
    it('should add correct query parameters when filtering posts', () => {
      const mockPosts: PostResponse[] = [];
      const content = 'test';
      const author = 'author';
      const fromDate = '2024-01-01T00:00:00';
      const toDate = '2024-01-01T23:59:59';
      const status = 'PUBLISHED';

      service.getFilteredPosts(content, author, fromDate, toDate, status).subscribe();

      const req = httpMock.expectOne(
        `${baseUrl}?content=test&author=author&fromDate=${fromDate}&toDate=${toDate}&status=PUBLISHED`
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });

    it('should handle undefined parameters correctly', () => {
      service.getFilteredPosts().subscribe();

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });

    it('should handle partial parameters', () => {
      service.getFilteredPosts('test', undefined, undefined, undefined, 'DRAFT').subscribe();

      const req = httpMock.expectOne(`${baseUrl}?content=test&status=DRAFT`);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });

  describe('getAllPostsByAuthor', () => {
    it('should retrieve posts for specific author', () => {
      const author = 'testAuthor';
      const mockPosts: PostResponse[] = [
        { id: 1, title: 'Test', content: 'Content', author: author, status: 'PUBLISHED', creationDate: '2024-01-01' }
      ];

      service.getAllPostsByAuthor(author).subscribe(posts => {
        expect(posts).toEqual(mockPosts);
      });

      const req = httpMock.expectOne(`${baseUrl}?author=${author}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });
  });

  describe('createPost', () => {
    it('should create a new post', () => {
      const postRequest: PostRequest = {
        title: 'New Post',
        content: 'Content',
        author: 'Author',
        status: 'DRAFT'
      };
      const mockResponse: PostResponse = {
        id: 1,
        ...postRequest,
        creationDate: '2024-01-01'
      };

      service.createPost(postRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(baseUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(postRequest);
      req.flush(mockResponse);
    });

    it('should handle error when creating post fails', () => {
      const postRequest: PostRequest = {
        title: 'New Post',
        content: 'Content',
        author: 'Author',
        status: 'DRAFT'
      };

      service.createPost(postRequest).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(400);
          expect(error.statusText).toBe('Bad Request');
        }
      });

      const req = httpMock.expectOne(baseUrl);
      req.flush('Invalid request', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('updatePost', () => {
    it('should update an existing post', () => {
      const postId = 1;
      const postRequest: PostRequest = {
        title: 'Updated Post',
        content: 'Updated Content',
        author: 'Author',
        status: 'PUBLISHED'
      };
      const mockResponse: PostResponse = {
        id: postId,
        ...postRequest,
        creationDate: '2024-01-01'
      };

      service.updatePost(postId, postRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(postRequest);
      req.flush(mockResponse);
    });
  });

  describe('getPostById', () => {
    it('should retrieve a post by ID', () => {
      const postId = 1;
      const mockPost: PostResponse = {
        id: postId,
        title: 'Test Post',
        content: 'Content',
        author: 'Author',
        status: 'PUBLISHED',
        creationDate: '2024-01-01'
      };

      service.getPostById(postId).subscribe(post => {
        expect(post).toEqual(mockPost);
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPost);
    });

    it('should handle not found error', () => {
      const postId = 999;

      service.getPostById(postId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
          expect(error.statusText).toBe('Not Found');
        }
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}`);
      req.flush('Post not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getPostsByStatus', () => {
    it('should retrieve posts by status', () => {
      const status = 'DRAFT';
      const mockPosts: PostResponse[] = [
        { id: 1, title: 'Draft Post', content: 'Content', author: 'Author', status: status, creationDate: '2024-01-01' }
      ];

      service.getPostsByStatus(status).subscribe(posts => {
        expect(posts).toEqual(mockPosts);
      });

      const req = httpMock.expectOne(`${baseUrl}/status/${status}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });
  });

  describe('updateStatus', () => {
    it('should update post status', () => {
      const postId = 1;
      const status = 'PUBLISHED';
      const mockResponse: PostResponse = {
        id: postId,
        title: 'Test Post',
        content: 'Content',
        author: 'Author',
        status: status,
        creationDate: '2024-01-01'
      };

      service.updateStatus(postId, status).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${baseUrl}/${postId}/status?status=${status}`);
      expect(req.request.method).toBe('PATCH');
      req.flush(mockResponse);
    });
  });

  describe('getNotificationsForAuthor', () => {
    it('should retrieve notifications for an author', () => {
      const author = 'testAuthor';
      const mockNotifications = [
        { message: 'New notification', author: 'Author', postAuthor: 'PostAuthor' }
      ];

      service.getNotificationsForAuthor(author).subscribe(notifications => {
        expect(notifications).toEqual(mockNotifications);
      });

      const req = httpMock.expectOne(`${baseUrl}/notifications/${author}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockNotifications);
    });

    it('should handle empty notifications', () => {
      const author = 'testAuthor';

      service.getNotificationsForAuthor(author).subscribe(notifications => {
        expect(notifications).toEqual([]);
      });

      const req = httpMock.expectOne(`${baseUrl}/notifications/${author}`);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });
});