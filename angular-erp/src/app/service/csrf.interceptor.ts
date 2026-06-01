import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { HttpXsrfTokenExtractor } from '@angular/common/http';

export const csrfInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenExtractor = inject(HttpXsrfTokenExtractor);

  if (['GET', 'HEAD'].includes(req.method)) {
    return next(req);
  }

  const token = tokenExtractor.getToken();
  if (token && !req.headers.has('X-XSRF-TOKEN')) {
    req = req.clone({ headers: req.headers.set('X-XSRF-TOKEN', token) });
  }

  return next(req);
};
