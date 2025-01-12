import { Component, OnInit } from '@angular/core';
import { PostService, NotificationResponse } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit {
  notifications: NotificationResponse[] = [];


  constructor(private postService: PostService, private authService: AuthService) {}

  ngOnInit(): void {
    const currentUser = this.authService.getUser();
    if (currentUser) {
      this.loadNotifications(currentUser);
    }
  }

  loadNotifications(author: string): void {
    this.postService.getNotificationsForAuthor(author).subscribe((notifications) => {
      this.notifications = notifications;
      this.notifications.reverse();
    });
  }
}