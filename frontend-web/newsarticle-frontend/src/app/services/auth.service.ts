import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private user: string | null = null;
  private role: string | null = null;

  // constructor() { }

  setUser(user: string): void {
    this.user = user;
    localStorage.setItem('user', user);
  }

  getUser(): string | null {
    return this.user || localStorage.getItem('user');
  }

  setRole(role: string): void {
    this.role = role;
    localStorage.setItem('role', role);
  }

  getRole(): string | null {
    return this.role || localStorage.getItem('role');
  }

  logout(): void {
    this.user = null;
    this.role = null;
    localStorage.removeItem('user');
    localStorage.removeItem('role');
  }
}
