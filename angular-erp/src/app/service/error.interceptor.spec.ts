import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { API_BASE_URL } from '../api-base-url.token';
import { errorInterceptor } from './error.interceptor';
import { ErrorNotificationService } from './error-notification.service';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let navigateSpy: ReturnType<typeof vi.fn>;
  let notifications: ErrorNotificationService;

  beforeEach(() => {
    sessionStorage.clear();
    navigateSpy = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: 'http://localhost:8080' },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    notifications = TestBed.inject(ErrorNotificationService);
  });

  afterEach(() => {
    httpMock.verify();
    notifications.clear();
    sessionStorage.clear();
  });

  function failWith(url: string, status: number, body: object | null = null): void {
    http.get(url).subscribe({ error: () => {} });
    httpMock.expectOne(url).flush(body, { status, statusText: 'Error' });
  }

  it('on 401 while logged in, clears the session and redirects to login with the expired flag', () => {
    sessionStorage.setItem('username', 'employee1');
    sessionStorage.setItem('isManager', 'false');

    failWith('/api/v1/orders', 401);

    expect(sessionStorage.getItem('username')).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith(['/login'], { queryParams: { sessionExpired: 'true' } });
    expect(notifications.message()).toBeNull();
  });

  it('redirects only once when several requests fail with 401 at the same time', () => {
    sessionStorage.setItem('username', 'employee1');

    http.get('/api/v1/orders').subscribe({ error: () => {} });
    http.get('/api/v1/products').subscribe({ error: () => {} });
    httpMock.expectOne('/api/v1/orders').flush(null, { status: 401, statusText: 'Unauthorized' });
    httpMock.expectOne('/api/v1/products').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });

  it('on 400, shows the backend message in the banner and does not redirect', () => {
    sessionStorage.setItem('username', 'employee1');

    failWith('/api/v1/orders', 400, { status: 400, message: 'customerId: must not be null' });

    expect(notifications.message()).toBe('customerId: must not be null');
    expect(navigateSpy).not.toHaveBeenCalled();
    expect(sessionStorage.getItem('username')).toBe('employee1');
  });

  it('leaves login errors to the login page', () => {
    sessionStorage.setItem('username', 'employee1');

    failWith('/api/auth/login', 401, { error: 'Invalid username or password' });

    expect(navigateSpy).not.toHaveBeenCalled();
    expect(notifications.message()).toBeNull();
  });
});
