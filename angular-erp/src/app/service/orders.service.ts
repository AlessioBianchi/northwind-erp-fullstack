import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderDetail } from '../layout/orders/order.model';
import { PageResponse } from '../page-response.model';
import { API_BASE_URL } from '../api-base-url.token';

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  private http = inject(HttpClient);
  private apiBaseUrl = `${inject(API_BASE_URL)}/api/v1/orders`;
  private httpOptions = { withCredentials: true };

  getPaginatedOrders(pageNumber: number, pageSize: number): Observable<PageResponse<Order>> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());

    return this.http.get<PageResponse<Order>>(this.apiBaseUrl, {
      ...this.httpOptions,
      params
    });
  }

  createOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(this.apiBaseUrl, order, this.httpOptions);
  }

  updateOrder(order: Order): Observable<Order> {
    return this.http.put<Order>(this.apiBaseUrl + '/' + order.orderId, order, this.httpOptions);
  }

  deleteOrder(orderId: number): Observable<void> {
    return this.http.delete<void>(this.apiBaseUrl + '/' + orderId, this.httpOptions);
  }

  getOrderDetails(orderId: number): Observable<OrderDetail[]> {
    return this.http.get<OrderDetail[]>(this.apiBaseUrl + '/' + orderId + '/details', this.httpOptions);
  }

  createOrderDetail(orderId: number, orderDetail: OrderDetail): Observable<OrderDetail> {
    return this.http.post<OrderDetail>(this.apiBaseUrl + '/' + orderId + '/details', orderDetail, this.httpOptions);
  }

  deleteOrderDetail(orderId: number, productId: number): Observable<void> {
    return this.http.delete<void>(this.apiBaseUrl + '/' + orderId + '/details/' + productId, this.httpOptions);
  }
}