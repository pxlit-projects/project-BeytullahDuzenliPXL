import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { PostEditorComponent } from './post-editor.component';
import { PostService, PostRequest, PostResponse } from '../../services/post.service';
import { ReviewService, ReviewResponse } from '../../services/review.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

describe('PostEditorComponent', () => {
  let component: PostEditorComponent;
  let fixture: ComponentFixture<PostEditorComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let reviewServiceSpy: jasmine.SpyObj<ReviewService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const postServiceMock = jasmine.createSpyObj('PostService', ['getPostById', 'updatePost', 'createPost']);
    const reviewServiceMock = jasmine.createSpyObj('ReviewService', ['getReviewForPost']);
    const authServiceMock = jasmine.createSpyObj('AuthService', ['getUser']);
    const routerMock = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [PostEditorComponent, FormsModule, RouterTestingModule],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: ReviewService, useValue: reviewServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: { id: null } },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PostEditorComponent);
    component = fixture.componentInstance;
    postServiceSpy = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    reviewServiceSpy = TestBed.inject(ReviewService) as jasmine.SpyObj<ReviewService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should not set isEditMode or load post if postId is null', () => {
      component.postId = null;

      component.ngOnInit();

      expect(component.isEditMode).toBeFalse();
      expect(postServiceSpy.getPostById).not.toHaveBeenCalled();
    });
  });

  describe('loadPost', () => {
    it('should load post and set postRequest', () => {
      const mockPost: PostResponse = {
        id: 1,
        title: 'Test Post',
        content: 'Test Content',
        author: 'Author',
        status: 'DRAFT',
        creationDate: '2024-01-01',
      };

      postServiceSpy.getPostById.and.returnValue(of(mockPost));

      component.postId = 1;
      component.loadPost();

      expect(postServiceSpy.getPostById).toHaveBeenCalledWith(1);
      expect(component.postRequest.title).toBe(mockPost.title);
    });

    it('should load review reason if the post status is REJECTED', () => {
      const mockPost: PostResponse = {
        id: 1,
        title: 'Rejected Post',
        content: 'Test Content',
        author: 'Author',
        status: 'REJECTED',
        creationDate: '2024-01-01',
      };

      const mockReview: ReviewResponse = {
        postId: 1,
        reason: 'Invalid content',
        postAuthor: 'Author',
        author: 'Reviewer',
        status: 'REJECTED',
      };

      postServiceSpy.getPostById.and.returnValue(of(mockPost));
      reviewServiceSpy.getReviewForPost.and.returnValue(of(mockReview));

      component.postId = 1;
      component.loadPost();

      expect(postServiceSpy.getPostById).toHaveBeenCalledWith(1);
      expect(reviewServiceSpy.getReviewForPost).toHaveBeenCalledWith(1);
      expect(component.reviewReason).toBe(mockReview.reason);
    });
  });

  describe('saveAsDraft', () => {
    it('should set status to DRAFT and call savePost', () => {
      spyOn(component, 'savePost');
  
      component.saveAsDraft();
  
      expect(component.postRequest.status).toBe('DRAFT');
      expect(component.savePost).toHaveBeenCalled();
    });
  });
  
  describe('submitForReview', () => {
    it('should set status to SUBMITTED and call savePost', () => {
      spyOn(component, 'savePost');
  
      component.submitForReview();
  
      expect(component.postRequest.status).toBe('SUBMITTED');
      expect(component.savePost).toHaveBeenCalled();
    });
  });
  
  describe('savePost', () => {
    it('should update post if in edit mode', () => {
      component.isEditMode = true;
      component.postId = 1;
      component.postRequest = {
        title: 'Updated Title',
        content: 'Updated Content',
        author: '',
        status: 'DRAFT',
      };
      authServiceSpy.getUser.and.returnValue('Author');
      postServiceSpy.updatePost.and.returnValue(of({} as PostResponse));
  
      component.savePost();
  
      expect(component.postRequest.author).toBe('Author');
      expect(postServiceSpy.updatePost).toHaveBeenCalledWith(1, component.postRequest);
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/myposts']);
    });
  
    it('should create new post if not in edit mode', () => {
      component.isEditMode = false;
      component.postRequest = {
        title: 'New Post',
        content: 'New Content',
        author: '',
        status: 'SUBMITTED',
      };
      authServiceSpy.getUser.and.returnValue('NewAuthor');
      postServiceSpy.createPost.and.returnValue(of({} as PostResponse));
  
      component.savePost();
  
      expect(component.postRequest.author).toBe('NewAuthor');
      expect(postServiceSpy.createPost).toHaveBeenCalledWith(component.postRequest);
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/myposts']);
    });
  });  

});