import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../layout/products/product.model';
import { PageResponse } from '../page-response.model';
import { API_BASE_URL } from '../api-base-url.token';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private http = inject(HttpClient);

  private apiBaseUrl = `${inject(API_BASE_URL)}/api/v1/products`;
  private httpOptions = { withCredentials: true };

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiBaseUrl, this.httpOptions);
  }
  
  getPaginatedProducts(pageNumber: number, pageSize: number, search?: string): Observable<PageResponse<Product>> {
    let params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<PageResponse<Product>>(this.apiBaseUrl, {
      ...this.httpOptions,
      params
    });
  }
  
  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiBaseUrl, product, this.httpOptions);
  }

  updateProduct(product: Product): Observable<Product> {
    return this.http.put<Product>(this.apiBaseUrl + '/' + product.productId, product, this.httpOptions);
  }
  
  deleteProduct(productId: number): Observable<void> {
    return this.http.delete<void>(this.apiBaseUrl + '/' + productId, this.httpOptions);
  }
}