import { Component, inject, OnInit } from '@angular/core';
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
  private chartInstance?: Chart;
  stats?: DashboardData;

  ngOnInit(): void {
    this.dashboardService.getDashboardStats().subscribe({
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
    const d = new Date(dateStr);
    const pad = (s: number) => s < 10 ? "0" + s : s;
    return [pad(d.getFullYear()), pad(d.getMonth() + 1), pad(d.getDate())].join("-");
  }

  ngOnDestroy(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }
}