import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SuppliersService } from './suppliers.service';
import { API_BASE_URL } from '../api-base-url.token';

describe('SuppliersService', () => {
  let service: SuppliersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SuppliersService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: 'http://localhost:8080' }
      ]
    });

    service = TestBed.inject(SuppliersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('getPaginatedSuppliers sends page params and no search by default', () => {
    service.getPaginatedSuppliers(1, 50).subscribe();

    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/suppliers');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('pageNumber')).toBe('1');
    expect(req.request.params.get('pageSize')).toBe('50');
    expect(req.request.params.has('search')).toBe(false);
    req.flush({ content: [] });
  });

  it('getPaginatedSuppliers sends the search param when given', () => {
    service.getPaginatedSuppliers(0, 50, 'exotic').subscribe();

    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/suppliers');
    expect(req.request.params.get('search')).toBe('exotic');
    req.flush({ content: [] });
  });
});
