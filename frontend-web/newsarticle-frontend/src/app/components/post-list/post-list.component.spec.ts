import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostListComponent } from './post-list.component';
import { PostService, PostResponse } from '../../services/post.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

describe('PostListComponent', () => {
  let component: PostListComponent;
  let fixture: ComponentFixture<PostListComponent>;
  let postServiceSpy: jasmine.SpyObj<PostService>;

  const mockPosts: PostResponse[] = [
    {
      id: 1,
      title: 'Post 1',
      content: 'Content 1',
      author: 'Author 1',
      status: 'PUBLISHED',
      creationDate: '2024-01-01T00:00:00',
    },
    {
      id: 2,
      title: 'Post 2',
      content: 'Content 2',
      author: 'Author 2',
      status: 'PUBLISHED',
      creationDate: '2024-01-02T00:00:00',
    },
  ];

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('PostService', ['getFilteredPosts']);

    await TestBed.configureTestingModule({
      imports: [PostListComponent, FormsModule, DatePipe],
      providers: [{ provide: PostService, useValue: spy }],
    }).compileComponents();

    fixture = TestBed.createComponent(PostListComponent);
    component = fixture.componentInstance;
    postServiceSpy = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;

    postServiceSpy.getFilteredPosts.and.returnValue(of(mockPosts));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should call fetchPosts on initialization', () => {
      spyOn(component, 'fetchPosts');
      component.ngOnInit();
      expect(component.fetchPosts).toHaveBeenCalled();
    });
  });

  describe('fetchPosts', () => {
    it('should fetch posts and update posts and filteredPosts', () => {
      component.fetchPosts();

      expect(postServiceSpy.getFilteredPosts).toHaveBeenCalledWith(
        component.searchQuery,
        component.selectedAuthor,
        component.fromDate ? component.fromDate + 'T00:00:00' : undefined,
        component.toDate ? component.toDate + 'T23:59:59' : undefined,
        'PUBLISHED'
      );
      expect(component.posts).toEqual(mockPosts.reverse());
      expect(component.filteredPosts).toEqual(mockPosts);
    });
  });

  describe('resetFilters', () => {
    it('should reset all filters and fetch posts', () => {
      spyOn(component, 'fetchPosts');

      component.searchQuery = 'test';
      component.selectedAuthor = 'author';
      component.fromDate = '2024-01-01';
      component.toDate = '2024-01-02';

      component.resetFilters();

      expect(component.searchQuery).toBe('');
      expect(component.selectedAuthor).toBe('');
      expect(component.fromDate).toBeNull();
      expect(component.toDate).toBeNull();
      expect(component.fetchPosts).toHaveBeenCalled();
    });
  });

  describe('applyFilters', () => {
    it('should call fetchPosts', () => {
      spyOn(component, 'fetchPosts');

      component.applyFilters();

      expect(component.fetchPosts).toHaveBeenCalled();
    });
  });

  describe('updateToDateLimit', () => {
    it('should reset toDate if it is less than fromDate', () => {
      component.fromDate = '2024-01-02';
      component.toDate = '2024-01-01';

      component.updateToDateLimit();

      expect(component.toDate).toBeNull();
    });

    it('should not reset toDate if it is valid', () => {
      component.fromDate = '2024-01-01';
      component.toDate = '2024-01-02';

      component.updateToDateLimit();

      expect(component.toDate).toBe('2024-01-02');
    });
  });
});