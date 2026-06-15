import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginData = {
    username: '',
    password: ''
  };

  hasError: boolean = false;
  hasLoggedOut: boolean = false;

  constructor(private router: Router, private authService: AuthService) {}

  onLogin(form: any): void {
    if (form.invalid) {
      return;
    }

    this.authService.login(this.loginData.username, this.loginData.password).subscribe({
      next: (response) => {
        this.hasError = false;
        this.router.navigate(['/layout/dashboard']);
      },
      error: (err) => {
        console.log(err);
        this.hasError = true;
      }
    });
  }
}