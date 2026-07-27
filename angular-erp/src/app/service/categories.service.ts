import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../layout/products/category.model';
import { API_BASE_URL } from '../api-base-url.token';

@Injectable({
  providedIn: 'root'
})
export class CategoriesService {
  private http = inject(HttpClient);

  private apiBaseUrl = `${inject(API_BASE_URL)}/api/v1/categories`;
  private httpOptions = { withCredentials: true };

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiBaseUrl, this.httpOptions);
  }
  
  createCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(this.apiBaseUrl, category, this.httpOptions);
  }

  updateCategory(category: Category): Observable<Category> {
    return this.http.put<Category>(this.apiBaseUrl + '/' + category.categoryId, category, this.httpOptions);
  }
  
  deleteCategory(categoryId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(this.apiBaseUrl + '/delete/' + categoryId, this.httpOptions);
  }
}