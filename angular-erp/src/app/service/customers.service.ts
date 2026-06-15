import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../layout/customers/customer.model';
import { PageResponse } from '../page-response.model';

@Injectable({
  providedIn: 'root'
})
export class CustomersService {
  private http = inject(HttpClient);
  private apiBaseUrl = 'http://localhost:8080/api/v1/customers'; 
  private httpOptions = { withCredentials: true };

  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiBaseUrl + '/all', this.httpOptions);
  }

  getPaginatedCustomers(pageNumber: number, pageSize: number): Observable<PageResponse<Customer>> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<PageResponse<Customer>>(this.apiBaseUrl + '/paginated', {
      ...this.httpOptions,
      params
    });
  }

  saveCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.apiBaseUrl + '/save', customer, this.httpOptions);
  }

  deleteCustomer(customerId: number): Observable<{ message: string }> {
    return this.http.get<{ message: string }>(this.apiBaseUrl + '/delete/' + customerId, this.httpOptions);
  }
}