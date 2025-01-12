import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      role: ['', [Validators.required]],
    });
  }

  login(): void {
    if (this.loginForm.valid) {
      const { username, role } = this.loginForm.value;
      this.authService.setUser(username);
      this.authService.setRole(role);
      this.router.navigate(['/posts']);
    } else {
      alert('Gelieve alle velden correct in te vullen!');
    }
  }

  get username() {
    return this.loginForm.get('username');
  }

  get role() {
    return this.loginForm.get('role');
  }
}