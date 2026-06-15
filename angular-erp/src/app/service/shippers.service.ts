import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Shipper } from '../layout/suppliers/shipper.model';

@Injectable({
  providedIn: 'root'
})
export class ShippersService {
  private http = inject(HttpClient);
  
  private apiBaseUrl = 'http://localhost:8080/api/v1/shippers'; 
  private httpOptions = { withCredentials: true };
  
  getAllShippers(): Observable<Shipper[]> {
    return this.http.get<Shipper[]>(this.apiBaseUrl, this.httpOptions);
  }
  
  saveShipper(shipper: Shipper): Observable<Shipper> {
    return this.http.post<Shipper>(this.apiBaseUrl + '/save', shipper, this.httpOptions);
  }
  
  deleteShipper(shipperId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(this.apiBaseUrl + '/delete/' + shipperId, this.httpOptions);
  }
}