import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductsService } from './products.service';
import { API_BASE_URL } from '../api-base-url.token';
import { Product } from '../layout/products/product.model';

describe('ProductsService', () => {
  let service: ProductsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProductsService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: 'http://localhost:8080' }
      ]
    });

    service = TestBed.inject(ProductsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getAllProducts sends a GET to the products endpoint', () => {
    service.getAllProducts().subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/products');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('updateProduct sends a PUT to the product-specific endpoint', () => {
    const product = { productId: 7 } as Product;

    service.updateProduct(product).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/products/7');
    expect(req.request.method).toBe('PUT');
    req.flush(product);
  });

  it('getPaginatedProducts sends a GET to the collection with page params', () => {
    service.getPaginatedProducts(2, 10).subscribe();

    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/products');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('pageNumber')).toBe('2');
    expect(req.request.params.get('pageSize')).toBe('10');
    req.flush({ content: [] });
  });

  it('deleteProduct sends a DELETE to the product-specific endpoint', () => {
    service.deleteProduct(7).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/products/7');
    expect(req.request.method).toBe('DELETE');
    req.flush(null, { status: 204, statusText: 'No Content' });
  });
});
