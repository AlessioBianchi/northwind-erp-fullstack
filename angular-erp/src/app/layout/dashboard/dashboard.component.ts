import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Chart, registerables } from 'chart.js';
import { type DashboardData } from './dashboard.model';
import { DashboardService } from '../../service/dashboard.service';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit{
  private dashboardService = inject(DashboardService);
  private destroyRef = inject(DestroyRef);
  private chartInstance?: Chart;
  stats?: DashboardData;

  ngOnInit(): void {
    this.dashboardService.getDashboardStats().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => {
        this.stats = data;
        this.updateChartWithData();
      },
      error: (err) => {
        console.error('Failed to load ERP metrics data:', err);
      }
    });
  }

  ngAfterViewInit(): void {
    this.renderChart();
  }

  renderChart(): void {
    const ctx = document.getElementById('categoryPieChart') as HTMLCanvasElement;
    if (!ctx) return;

    this.chartInstance = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: ['Loading Data...'],
        datasets: [{
          data: [0],
          backgroundColor: ['#e9ecef']
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'bottom'
          }
        }
      }
    });
  }

  updateChartWithData(): void {
    if (!this.chartInstance || !this.stats || !this.stats.ordersByCategory) return;

    const categoriesMap = this.stats.ordersByCategory;
    
    const labels = Object.keys(categoriesMap);
    const dataValues = Object.values(categoriesMap);
    
    const colorPalette = [
      '#0d6efd', '#6f42c1', '#d63384', '#fd7e14', '#198754', '#20c997', '#0dcaf0'
    ];
    const backgroundColors = labels.map((_, i) => colorPalette[i % colorPalette.length]);
    
    this.chartInstance.data.labels = labels;
    this.chartInstance.data.datasets[0].data = dataValues;
    this.chartInstance.data.datasets[0].backgroundColor = backgroundColors;
    
    this.chartInstance.update();
  }

  getFormatDate(dateStr: string): string {
    if (!dateStr) return '';
    // The backend sends a date-only ISO string ("yyyy-MM-dd"). Take the date
    // part as-is instead of parsing it into a Date, which would interpret it
    // as UTC midnight and shift the displayed day in negative-UTC timezones.
    return dateStr.split('T')[0];
  }

  ngOnDestroy(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }
}