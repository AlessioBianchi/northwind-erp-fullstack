import { TestBed } from '@angular/core/testing';
import { ErrorNotificationService } from './error-notification.service';

describe('ErrorNotificationService', () => {
  let service: ErrorNotificationService;

  beforeEach(() => {
    vi.useFakeTimers();
    service = TestBed.inject(ErrorNotificationService);
  });

  afterEach(() => {
    service.clear();
    vi.useRealTimers();
  });

  it('show() defaults to an error', () => {
    service.show('Freight cannot be negative.');
    expect(service.message()).toBe('Freight cannot be negative.');
    expect(service.type()).toBe('error');
  });

  it('success() shows a success message', () => {
    service.success('Order saved successfully!');
    expect(service.message()).toBe('Order saved successfully!');
    expect(service.type()).toBe('success');
  });

  it('auto-dismisses after 3 seconds', () => {
    service.success('Saved.');
    vi.advanceTimersByTime(2999);
    expect(service.message()).toBe('Saved.');
    vi.advanceTimersByTime(1);
    expect(service.message()).toBeNull();
  });

  it('a new message restarts the dismiss timer', () => {
    service.show('First');
    vi.advanceTimersByTime(2000);
    service.success('Second');
    vi.advanceTimersByTime(2000);
    expect(service.message()).toBe('Second');
    vi.advanceTimersByTime(1000);
    expect(service.message()).toBeNull();
  });
});
