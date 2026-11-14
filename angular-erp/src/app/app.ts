import { Component } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { ErrorBannerComponent } from './error-banner/error-banner.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ErrorBannerComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
