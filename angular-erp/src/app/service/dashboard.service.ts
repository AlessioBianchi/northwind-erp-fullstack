import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardData } from '../layout/dashboard/dashboard.model';
import { API_BASE_URL } from '../api-base-url.token';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = `${inject(API_BASE_URL)}/api/v1/dashboard/stats`;

  getDashboardStats(): Observable<DashboardData> {
    return this.http.get<DashboardData>(this.apiUrl, { 
      withCredentials: true
    });
  }
}