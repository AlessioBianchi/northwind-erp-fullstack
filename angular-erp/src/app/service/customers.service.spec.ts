import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CustomersService } from './customers.service';
import { API_BASE_URL } from '../api-base-url.token';

describe('CustomersService', () => {
  let service: CustomersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CustomersService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: 'http://localhost:8080' }
      ]
    });

    service = TestBed.inject(CustomersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getPaginatedCustomers sends page params and no search by default', () => {
    service.getPaginatedCustomers(1, 50).subscribe();

    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/customers');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('pageNumber')).toBe('1');
    expect(req.request.params.get('pageSize')).toBe('50');
    expect(req.request.params.has('search')).toBe(false);
    req.flush({ content: [] });
  });

  it('getPaginatedCustomers sends the search param when given', () => {
    service.getPaginatedCustomers(0, 50, 'alfki').subscribe();

    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/customers');
    expect(req.request.params.get('search')).toBe('alfki');
    req.flush({ content: [] });
  });
});
