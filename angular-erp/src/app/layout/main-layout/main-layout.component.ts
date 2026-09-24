import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../../api-base-url.token';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent implements OnInit{
  private authService = inject(AuthService);
  private apiBaseUrl = inject(API_BASE_URL);
  private destroyRef = inject(DestroyRef);
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
    this.http.post(`${this.apiBaseUrl}/api/auth/logout`, {}, { withCredentials: true }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      complete: () => {
        this.authService.logout();
        this.router.navigate(['/login'], { queryParams: { loggedOut: 'true' } });
      },
      error: () => {
        this.authService.logout();
        this.router.navigate(['/login'], { queryParams: { loggedOut: 'true' } });
      }
    });
  }
}