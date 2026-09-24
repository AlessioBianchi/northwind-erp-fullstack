import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { API_BASE_URL } from '../api-base-url.token';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_BASE_URL, useValue: 'http://localhost:8080' }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('stores username and isManager in sessionStorage on successful login', () => {
    service.login('alessio', 'secret').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'ok', username: 'alessio', isManager: true });

    expect(sessionStorage.getItem('username')).toBe('alessio');
    expect(sessionStorage.getItem('isManager')).toBe('true');
  });

  it('getUsernameLogged falls back to sessionStorage when nothing is cached in memory', () => {
    sessionStorage.setItem('username', 'fromStorage');
    expect(service.getUsernameLogged()).toBe('fromStorage');
  });

  it('isUserManager falls back to the sessionStorage flag when nothing is cached in memory', () => {
    sessionStorage.setItem('isManager', 'true');
    expect(service.isUserManager()).toBe(true);
  });

  it('logout clears both sessionStorage and the in-memory cache', () => {
    service.login('alessio', 'secret').subscribe();
    httpMock.expectOne('http://localhost:8080/api/auth/login')
      .flush({ message: 'ok', username: 'alessio', isManager: true });

    service.logout();

    expect(sessionStorage.getItem('username')).toBeNull();
    expect(sessionStorage.getItem('isManager')).toBeNull();
    // Asserted via the public getters (not the private fields) so this test
    // catches a logout() that clears sessionStorage but forgets to reset the
    // in-memory cache, which would otherwise keep returning the stale, truthy value.
    expect(service.getUsernameLogged()).toBeNull();
    expect(service.isUserManager()).toBe(false);
  });
});
