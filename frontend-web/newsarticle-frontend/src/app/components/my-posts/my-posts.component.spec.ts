import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MyPostsComponent } from './my-posts.component';
import { PostService, PostResponse } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('MyPostsComponent', () => {
  let component: MyPostsComponent;
  let fixture: ComponentFixture<MyPostsComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockPosts: PostResponse[] = [
    {
      id: 1,
      title: 'Test Post 1',
      content: 'Test Content 1',
      author: 'testUser',
      status: 'PUBLISHED',
      creationDate: '2024-01-01'
    },
    {
      id: 2,
      title: 'Test Post 2',
      content: 'Test Content 2',
      author: 'testUser',
      status: 'DRAFT',
      creationDate: '2024-01-02'
    }
  ];

  beforeEach(async () => {
    postServiceSpy = jasmine.createSpyObj('PostService', ['getAllPostsByAuthor']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getUser']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [MyPostsComponent, DatePipe],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MyPostsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty posts array', () => {
    expect(component.myPosts).toEqual([]);
  });

  it('should not load posts when user is not authenticated', () => {
    authServiceSpy.getUser.and.returnValue(null);

    component.ngOnInit();
    fixture.detectChanges();

    expect(authServiceSpy.getUser).toHaveBeenCalled();
    expect(postServiceSpy.getAllPostsByAuthor).not.toHaveBeenCalled();
    expect(component.myPosts).toEqual([]);
  });

  it('should navigate to create post page when createNewPost is called', () => {
    component.createNewPost();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/myposts/create']);
  });

  it('should navigate to edit post page with correct ID when editPost is called', () => {
    const postId = 1;

    component.editPost(postId);

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/myposts/edit', postId]);
  });

  it('should display "Nieuwe Post Aanmaken" button', () => {
    fixture.detectChanges();

    const button = fixture.debugElement.query(By.css('button'));
    
    expect(button.nativeElement.textContent.trim()).toBe('Nieuwe Post Aanmaken');
  });

  it('should display posts when available', () => {
    authServiceSpy.getUser.and.returnValue('testUser');
    postServiceSpy.getAllPostsByAuthor.and.returnValue(of(mockPosts));
    
    component.ngOnInit();
    fixture.detectChanges();

    const postElements = fixture.debugElement.queryAll(By.css('.w-full.max-w-4xl.mb-6'));
    expect(postElements.length).toBe(mockPosts.length);
    
    const firstPost = postElements[0];
    expect(firstPost.query(By.css('h2')).nativeElement.textContent)
      .toContain('Test Post 1');
    expect(firstPost.query(By.css('.text-gray-700')).nativeElement.textContent)
      .toContain('Test Content 1');
  });

  it('should display empty message when no posts available', () => {
    authServiceSpy.getUser.and.returnValue('testUser');
    postServiceSpy.getAllPostsByAuthor.and.returnValue(of([]));
    
    component.ngOnInit();
    fixture.detectChanges();

    const emptyMessage = fixture.debugElement.query(By.css('.text-gray-500.text-center'));
    expect(emptyMessage.nativeElement.textContent).toContain('U heeft nog geen posts.');
  });

  it('should trigger editPost when edit button is clicked', () => {
    authServiceSpy.getUser.and.returnValue('testUser');
    postServiceSpy.getAllPostsByAuthor.and.returnValue(of(mockPosts));
    component.ngOnInit();
    fixture.detectChanges();
    
    const editButton = fixture.debugElement.query(By.css('button[class*="bg-green-600"]'));
    editButton.triggerEventHandler('click', null);

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/myposts/edit', 1]);
  });

  it('should trigger createNewPost when create button is clicked', () => {
    fixture.detectChanges();
    
    const createButton = fixture.debugElement.query(By.css('button'));
    createButton.triggerEventHandler('click', null);

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/myposts/create']);
  });

  it('should format date correctly', () => {
    authServiceSpy.getUser.and.returnValue('testUser');
    postServiceSpy.getAllPostsByAuthor.and.returnValue(of(mockPosts));
    
    component.ngOnInit();
    fixture.detectChanges();

    const dateElement = fixture.debugElement.query(By.css('.text-sm.text-gray-400'));
    expect(dateElement.nativeElement.textContent).toContain('2024-01-01');
  });
});