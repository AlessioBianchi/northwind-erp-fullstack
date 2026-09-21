import { Component, inject } from '@angular/core';
import { ErrorNotificationService } from '../service/error-notification.service';

@Component({
  selector: 'app-error-banner',
  standalone: true,
  templateUrl: './error-banner.component.html'
})
export class ErrorBannerComponent {
  protected readonly notificationService = inject(ErrorNotificationService);
}
