import { Routes } from '@angular/router';
import { PostListComponent } from './components/post-list/post-list.component';
import { PostEditorComponent } from './components/post-editor/post-editor.component';
import { PostDetailComponent } from './components/post-detail/post-detail.component';
import { LoginComponent } from './components/login/login.component';
import { MyPostsComponent } from './components/my-posts/my-posts.component';
import { NotificationsComponent } from './components/notifications/notifications.component';
import { authGuard } from './auth.guard';
import { ReviewPostsComponent } from './components/review-posts/review-posts.component';
import { ReviewPostDetailComponent } from './components/review-post-detail/review-post-detail.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full'},
  { path: 'login', component: LoginComponent },
  { path: 'posts', component: PostListComponent, canActivate: [authGuard], data: { roles: ['redacteur', 'gebruiker'] } },
  { path: 'posts/:id', component: PostDetailComponent, canActivate: [authGuard], data: { roles: ['redacteur', 'gebruiker'] } },
  { path: 'myposts', component: MyPostsComponent, canActivate: [authGuard], data: { roles: ['redacteur'] } },
  { path: 'myposts/create', component: PostEditorComponent, canActivate: [authGuard], data: { roles: ['redacteur'] } },
  { path: 'myposts/edit/:id', component: PostEditorComponent, canActivate: [authGuard], data: { roles: ['redacteur'] } },
  { path: 'review', component: ReviewPostsComponent, canActivate: [authGuard], data: { roles: ['redacteur'] } },
  { path: 'review/:id', component: ReviewPostDetailComponent, canActivate: [authGuard], data: { roles: ['redacteur'] } },
  { path: 'notifications', component: NotificationsComponent, canActivate: [authGuard], data: { roles: ['redacteur'] } },
];