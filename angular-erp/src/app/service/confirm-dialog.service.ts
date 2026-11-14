import { Injectable, signal } from '@angular/core';

export interface ConfirmRequest {
  title: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {
  readonly request = signal<ConfirmRequest | null>(null);
  private resolveChoice: ((choice: boolean) => void) | null = null;

  confirm(message: string, title = 'Confirm deletion'): Promise<boolean> {
    this.resolveChoice?.(false);
    this.request.set({ title, message });
    return new Promise(resolve => this.resolveChoice = resolve);
  }

  answer(choice: boolean): void {
    this.resolveChoice?.(choice);
    this.resolveChoice = null;
    this.request.set(null);
  }
}
