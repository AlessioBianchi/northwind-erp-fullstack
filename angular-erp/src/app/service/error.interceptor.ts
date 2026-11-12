import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ErrorNotificationService } from './error-notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const errorNotificationService = inject(ErrorNotificationService);
  const authService = inject(AuthService);
  const router = inject(Router);

  if (req.url.includes('/api/auth/login')) {
    return next(req);
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // The backend session is gone (timeout or restart) while sessionStorage still says "logged in".
        // Only the first 401 redirects: logout() clears that state, so parallel requests failing too are ignored.
        if (authService.getUsernameLogged()) {
          authService.logout();
          router.navigate(['/login'], { queryParams: { sessionExpired: 'true' } });
        }
        return throwError(() => error);
      }

      const message = error.error?.message || `Request failed (${error.status || 'network error'})`;
      errorNotificationService.show(message);
      return throwError(() => error);
    })
  );
};
