import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { EmployeesService } from './employees.service';
import { API_BASE_URL } from '../api-base-url.token';

describe('EmployeesService', () => {
  let service: EmployeesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EmployeesService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: 'http://localhost:8080' }
      ]
    });

    service = TestBed.inject(EmployeesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('changeMyPassword sends a PUT to /me/password with both passwords', () => {
    service.changeMyPassword('oldSecret1', 'newSecret1').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/employees/me/password');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ currentPassword: 'oldSecret1', newPassword: 'newSecret1' });
    req.flush(null, { status: 204, statusText: 'No Content' });
  });

  it('getPaginatedEmployees sends page params and no search by default', () => {
    service.getPaginatedEmployees(1, 50).subscribe();

    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/employees');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('pageNumber')).toBe('1');
    expect(req.request.params.get('pageSize')).toBe('50');
    expect(req.request.params.has('search')).toBe(false);
    req.flush({ content: [] });
  });

  it('getPaginatedEmployees sends the search param when given', () => {
    service.getPaginatedEmployees(0, 50, 'sales').subscribe();

    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/employees');
    expect(req.request.params.get('search')).toBe('sales');
    req.flush({ content: [] });
  });
});
