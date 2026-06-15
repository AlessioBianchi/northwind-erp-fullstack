import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Supplier } from '../layout/suppliers/supplier.model';
import { PageResponse } from '../page-response.model';

@Injectable({
  providedIn: 'root'
})
export class SuppliersService {
  private http = inject(HttpClient);
  
  private apiBaseUrl = 'http://localhost:8080/suppliers'; 
  private httpOptions = { withCredentials: true };

  getAllSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(this.apiBaseUrl + '/all', this.httpOptions);
  }
  
  getPaginatedSuppliers(pageNumber: number, pageSize: number): Observable<PageResponse<Supplier>> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<PageResponse<Supplier>>(this.apiBaseUrl + '/paginated', {
      ...this.httpOptions,
      params
    });
  }
  
  saveSupplier(supplier: Supplier): Observable<Supplier> {
    return this.http.post<Supplier>(this.apiBaseUrl + '/save', supplier, this.httpOptions);
  }
  
  deleteSupplier(supplierId: number): Observable<{ message: string }> {
    return this.http.get<{ message: string }>(this.apiBaseUrl + '/delete/' + supplierId, this.httpOptions);
  }
}