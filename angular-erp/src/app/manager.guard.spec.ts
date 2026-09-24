import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { managerGuard } from './manager.guard';
import { AuthService } from './service/auth.service';

describe('managerGuard', () => {
  let authServiceSpy: { isUserManager: () => boolean };
  let navigateSpy: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    authServiceSpy = { isUserManager: () => false };
    navigateSpy = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: { navigate: navigateSpy } }
      ]
    });
  });

  it('allows activation when the logged-in user is a manager', () => {
    authServiceSpy.isUserManager = () => true;

    const result = TestBed.runInInjectionContext(() => managerGuard({} as any, {} as any));

    expect(result).toBe(true);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('redirects to the 404 page and blocks activation when the user is not a manager', () => {
    const result = TestBed.runInInjectionContext(() => managerGuard({} as any, {} as any));

    expect(result).toBe(false);
    expect(navigateSpy).toHaveBeenCalledWith(['/404']);
  });
});
