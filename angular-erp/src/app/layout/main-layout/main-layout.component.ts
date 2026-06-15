import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent implements OnInit{
  private authService = inject(AuthService);
  usernameLogged: string | null = '';
  isManager = false;

  isSidebarCollapsed = false;

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    this.usernameLogged = this.authService.getUsernameLogged();
    this.isManager = this.authService.isUserManager();
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  onLogout(): void {
    this.http.post('http://localhost:8080/api/auth/logout', {}, { withCredentials: true }).subscribe({
      complete: () => {
        sessionStorage.clear();
        this.router.navigate(['/login']);
      },
      error: () => {
        sessionStorage.clear();
        this.router.navigate(['/login']);
      }
    });
  }
}