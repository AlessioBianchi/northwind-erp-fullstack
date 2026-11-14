import { Component, inject } from '@angular/core';
import { ConfirmDialogService } from '../service/confirm-dialog.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  templateUrl: './confirm-dialog.component.html',
  host: { '(document:keydown.escape)': 'onEscape()' }
})
export class ConfirmDialogComponent {
  protected readonly confirmDialog = inject(ConfirmDialogService);

  protected onEscape(): void {
    if (this.confirmDialog.request()) {
      this.confirmDialog.answer(false);
    }
  }
}
