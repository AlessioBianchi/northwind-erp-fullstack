import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderDetail } from '../layout/orders/order.model';
import { PageResponse } from '../page-response.model';

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  private http = inject(HttpClient); 
  private apiBaseUrl = 'http://localhost:8080/api/v1/orders';
  private httpOptions = { withCredentials: true };

  getPaginatedOrders(pageNumber: number, pageSize: number): Observable<PageResponse<Order>> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<PageResponse<Order>>(this.apiBaseUrl + '/paginated', {
      ...this.httpOptions,
      params
    });
  }

  saveOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(this.apiBaseUrl + '/save', order, this.httpOptions);
  }

  updateOrder(order: Order): Observable<Order> {
    return this.http.put<Order>(this.apiBaseUrl + '/save', order, this.httpOptions);
  }

  deleteOrder(orderId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(this.apiBaseUrl + '/delete/' + orderId, this.httpOptions);
  }

  getOrderDetails(orderId: number): Observable<OrderDetail[]> {
    return this.http.get<OrderDetail[]>(this.apiBaseUrl + '/details/' + orderId, this.httpOptions);
  }

  saveOrderDetail(orderDetail: OrderDetail): Observable<OrderDetail> {
    return this.http.post<OrderDetail>(this.apiBaseUrl + '/details/save', orderDetail, this.httpOptions);
  }

  updateOrderDetail(orderDetail: OrderDetail): Observable<OrderDetail> {
    return this.http.put<OrderDetail>(this.apiBaseUrl + '/details/save', orderDetail, this.httpOptions);
  }

  deleteOrderDetail(orderDetail: OrderDetail): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(this.apiBaseUrl + '/details/delete', {
      ...this.httpOptions,
      body: orderDetail
    });
  }
}