import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../layout/products/category.model';

@Injectable({
  providedIn: 'root'
})
export class CategoriesService {
  private http = inject(HttpClient);
  
  private apiBaseUrl = 'http://localhost:8080/api/v1/categories'; 
  private httpOptions = { withCredentials: true };

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiBaseUrl, this.httpOptions);
  }
  
  saveCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(this.apiBaseUrl + '/save', category, this.httpOptions);
  }
  
  deleteCategory(categoryId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(this.apiBaseUrl + '/delete/' + categoryId, this.httpOptions);
  }
}