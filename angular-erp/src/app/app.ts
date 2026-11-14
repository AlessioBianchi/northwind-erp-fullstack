import { Component } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { ErrorBannerComponent } from './error-banner/error-banner.component';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ErrorBannerComponent, ConfirmDialogComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
