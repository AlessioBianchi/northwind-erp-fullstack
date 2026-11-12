import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { OrdersService } from './orders.service';
import { API_BASE_URL } from '../api-base-url.token';
import { OrderDetail } from '../layout/orders/order.model';

describe('OrdersService', () => {
  let service: OrdersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        OrdersService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: 'http://localhost:8080' }
      ]
    });

    service = TestBed.inject(OrdersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getOrderDetails sends a GET to the order details sub-resource', () => {
    service.getOrderDetails(5).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/orders/5/details');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('createOrderDetail sends a POST to the order details sub-resource', () => {
    const detail = { orderId: 5, productId: 50, unitPrice: 0, quantity: 2, discount: 0 } as OrderDetail;

    service.createOrderDetail(5, detail).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/orders/5/details');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(detail);
    req.flush(detail, { status: 201, statusText: 'Created' });
  });

  it('deleteOrderDetail sends a DELETE with both ids in the path and no body', () => {
    service.deleteOrderDetail(5, 50).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/orders/5/details/50');
    expect(req.request.method).toBe('DELETE');
    expect(req.request.body).toBeNull();
    req.flush(null, { status: 204, statusText: 'No Content' });
  });

  it('deleteOrder sends a DELETE to the order-specific endpoint', () => {
    service.deleteOrder(5).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/orders/5');
    expect(req.request.method).toBe('DELETE');
    req.flush(null, { status: 204, statusText: 'No Content' });
  });
});
