import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['setUser', 'setRole']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty username and role', () => {
    expect(component.username?.value).toBe('');
    expect(component.role?.value).toBe('');
  });

  it('should successfully login with valid username and role', () => {
    component.username?.setValue('testUser');
    component.role?.setValue('admin');

    component.login();

    expect(authServiceSpy.setUser).toHaveBeenCalledWith('testUser');
    expect(authServiceSpy.setRole).toHaveBeenCalledWith('admin');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/posts']);
  });

  it('should show alert when username is missing', () => {
    component.username?.setValue('');
    component.role?.setValue('admin');
    spyOn(window, 'alert');
  
    component.login();
  
    expect(window.alert).toHaveBeenCalledWith('Gelieve alle velden correct in te vullen!');
    expect(authServiceSpy.setUser).not.toHaveBeenCalled();
    expect(authServiceSpy.setRole).not.toHaveBeenCalled();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });
  
  it('should show alert when role is missing', () => {
    component.username?.setValue('testUser');
    component.role?.setValue('');
    spyOn(window, 'alert');
  
    component.login();
  
    expect(window.alert).toHaveBeenCalledWith('Gelieve alle velden correct in te vullen!');
    expect(authServiceSpy.setUser).not.toHaveBeenCalled();
    expect(authServiceSpy.setRole).not.toHaveBeenCalled();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });  
});