import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from './service/auth.service';

describe('authGuard', () => {
  let authServiceSpy: { getUsernameLogged: () => string | null };
  let navigateSpy: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    authServiceSpy = { getUsernameLogged: () => null };
    navigateSpy = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });
  });

  it('allows activation when a user is logged in', () => {
    authServiceSpy.getUsernameLogged = () => 'alessio';

    const result = TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));

    expect(result).toBe(true);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('redirects to /login and blocks activation when no user is logged in', () => {
    const result = TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));

    expect(result).toBe(false);
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
