import { Injectable, signal } from '@angular/core';

const AUTO_DISMISS_MS = 3000;

@Injectable({
  providedIn: 'root'
})
export class ErrorNotificationService {
  readonly message = signal<string | null>(null);
  private dismissTimer: ReturnType<typeof setTimeout> | null = null;

  show(message: string): void {
    this.message.set(message);

    if (this.dismissTimer) {
      clearTimeout(this.dismissTimer);
    }
    this.dismissTimer = setTimeout(() => this.clear(), AUTO_DISMISS_MS);
  }

  clear(): void {
    this.message.set(null);
    if (this.dismissTimer) {
      clearTimeout(this.dismissTimer);
      this.dismissTimer = null;
    }
  }
}
