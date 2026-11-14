import { Injectable, signal } from '@angular/core';

const AUTO_DISMISS_MS = 3000;

export type NotificationType = 'success' | 'error';

@Injectable({
  providedIn: 'root'
})
export class ErrorNotificationService {
  readonly message = signal<string | null>(null);
  readonly type = signal<NotificationType>('error');
  private dismissTimer: ReturnType<typeof setTimeout> | null = null;

  show(message: string, type: NotificationType = 'error'): void {
    this.message.set(message);
    this.type.set(type);

    if (this.dismissTimer) {
      clearTimeout(this.dismissTimer);
    }
    this.dismissTimer = setTimeout(() => this.clear(), AUTO_DISMISS_MS);
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  clear(): void {
    this.message.set(null);
    if (this.dismissTimer) {
      clearTimeout(this.dismissTimer);
      this.dismissTimer = null;
    }
  }
}
