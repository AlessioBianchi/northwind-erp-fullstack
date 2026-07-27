import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../api-base-url.token';

interface LoginResponse {
  message: string;
  username: string;
  isManager: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${inject(API_BASE_URL)}/api/auth/login`;

  private usernameLogged: string | null = null;
  private isManager: boolean = false;

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    const body = new HttpParams()
      .set('username', username)
      .set('password', password);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    return this.http.post<LoginResponse>(this.apiUrl, body.toString(), { 
      headers, 
      withCredentials: true 
    }).pipe(
      tap(response => {
        if (response && response.username) {
          this.usernameLogged = response.username;
          this.isManager = response.isManager;
          
          sessionStorage.setItem('username', response.username);
          sessionStorage.setItem('isManager', String(response.isManager));
        }
      })
    );
  }

  getUsernameLogged(): string | null {
    if (!this.usernameLogged) {
      this.usernameLogged = sessionStorage.getItem('username');
    }
    return this.usernameLogged;
  }

  isUserManager(): boolean {
    if (!this.isManager) {
      const storedManagerFlag = sessionStorage.getItem('isManager');
      this.isManager = storedManagerFlag === 'true';
    }
    return this.isManager;
  }
}