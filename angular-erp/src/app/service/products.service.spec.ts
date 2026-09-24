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
});
