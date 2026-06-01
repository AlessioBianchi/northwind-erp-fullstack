import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';

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

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.usernameLogged = this.authService.getUsernameLogged();
    this.isManager = this.authService.isUserManager();
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  onLogout(): void {
    // localStorage.removeItem('isLoggedIn');
    this.router.navigate(['/login']);
  }
}