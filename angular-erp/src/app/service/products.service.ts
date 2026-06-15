import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../layout/products/product.model';
import { PageResponse } from '../page-response.model';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private http = inject(HttpClient);
  
  private apiBaseUrl = 'http://localhost:8080/products'; 
  private httpOptions = { withCredentials: true };

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiBaseUrl + '/all', this.httpOptions);
  }
  
  getPaginatedProducts(pageNumber: number, pageSize: number): Observable<PageResponse<Product>> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<PageResponse<Product>>(this.apiBaseUrl + '/paginated', {
      ...this.httpOptions,
      params
    });
  }
  
  saveProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiBaseUrl + '/save', product, this.httpOptions);
  }
  
  deleteProduct(productId: number): Observable<{ message: string }> {
    return this.http.get<{ message: string }>(this.apiBaseUrl + '/delete/' + productId, this.httpOptions);
  }
}