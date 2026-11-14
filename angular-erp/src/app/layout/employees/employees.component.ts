import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, finalize, Subject, Subscription } from 'rxjs';
import { Employee } from './employee.model';
import { EmployeesService } from '../../service/employees.service';
import { LoadingSpinnerComponent } from '../../loading-spinner/loading-spinner.component';
import { ErrorNotificationService } from '../../service/error-notification.service';
import { ConfirmDialogService } from '../../service/confirm-dialog.service';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingSpinnerComponent],
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css'
})
export class EmployeesComponent implements OnInit {
  private employeesService = inject(EmployeesService);
  private destroyRef = inject(DestroyRef);
  private notifications = inject(ErrorNotificationService);
  private confirmDialog = inject(ConfirmDialogService);

  currentPage = 1;
  pageSize = 50;
  totalPages = 0;
  totalElements = 0;
  isLoading = false;
  isDeleting = false;

  selectedEmployeeId: number | null = null;
  currentWorkspaceState: 'empty' | 'edit' | 'new' = 'empty';
  
  badgeText = '';
  badgeClass = 'd-none';
  
  employeesList: Employee[] = [];
  allEmployeesLookupList: Employee[] = [];
  
  activeEmployeeForm: Partial<Employee> = {};
  searchQuery = '';
  private searchInput$ = new Subject<string>();
  private listSubscription?: Subscription;

  ngOnInit(): void {
    this.searchInput$.pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(query => {
        this.searchQuery = query;
        this.currentPage = 1;
        this.loadPaginatedEmployees();
      });
    this.loadPaginatedEmployees();
    this.preloadDropdownRelationshipDependencies();
  }

  loadPaginatedEmployees(): void {
    const apiIndex = this.currentPage - 1;

    this.listSubscription?.unsubscribe();
    this.isLoading = true;
    this.listSubscription = this.employeesService.getPaginatedEmployees(apiIndex, this.pageSize, this.searchQuery)
      .pipe(finalize(() => this.isLoading = false), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.employeesList = res.content;
          this.totalElements = res.page.totalElements;
          this.totalPages = res.page.totalPages;
        },
        error: (err) => console.error('Error fetching paginated corporate roster list:', err)
      });
  }

  preloadDropdownRelationshipDependencies(): void {
    // Fetches everyone so any employee can be assigned as a manager
    this.employeesService.getAllByOrderByEmployeeIdDesc().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (data) => this.allEmployeesLookupList = data,
      error: (err) => console.error('Error syncing management roster dictionary listings:', err)
    });
  }

  onSearchInput(event: Event): void {
    this.searchInput$.next((event.target as HTMLInputElement).value.trim());
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.loadPaginatedEmployees();
  }
  
  selectEmployee(employee: Employee): void {
    this.selectedEmployeeId = employee.employeeId ?? null;
    this.currentWorkspaceState = 'edit';

    this.badgeText = `Employee #${employee.employeeId}`;
    this.badgeClass = 'badge bg-primary text-white';

    this.activeEmployeeForm = JSON.parse(JSON.stringify(employee));
  }

  initializeNewEmployeeForm(): void {
    this.selectedEmployeeId = null;
    this.currentWorkspaceState = 'new';
    
    this.badgeText = 'New Profile Template';
    this.badgeClass = 'badge bg-success text-white';

    this.activeEmployeeForm = {
      firstName: '',
      lastName: '',
      title: '',
      birthDate: '',
      hireDate: new Date().toISOString().substring(0, 10),
      address: '',
      city: '',
      region: '',
      postalCode: '',
      country: '',
      homePhone: '',
      extension: '',
      username: '',
      password: '',
      reportsTo: undefined
    };
  }

  cancelEmployeeWorkspaceEdit(): void {
    this.selectedEmployeeId = null;
    this.currentWorkspaceState = 'empty';
    this.badgeClass = 'd-none';
    this.activeEmployeeForm = {};
  }

  saveEmployeeHeader(event: Event): void {
    event.preventDefault();

    const employee = this.activeEmployeeForm as Employee;
    if (employee.password && employee.password.length < 8) {
      this.notifications.show('Password must be at least 8 characters.');
      return;
    }

    const request$ = employee.employeeId
      ? this.employeesService.update(employee)
      : this.employeesService.create(employee);

    request$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (savedResult) => {
          this.notifications.success('Employee profile record saved successfully.');
          this.loadPaginatedEmployees();
          this.preloadDropdownRelationshipDependencies();
          this.cancelEmployeeWorkspaceEdit();
        },
        error: (err) => console.error('Error processing server authentication profile updates:', err)
    });
  }

  async deleteActiveEmployee(): Promise<void> {
    if (!this.selectedEmployeeId || this.isDeleting) return;

    if (await this.confirmDialog.confirm('Are you sure you want to drop this worker profile history record?')) {
      this.isDeleting = true;
      this.employeesService.delete(this.selectedEmployeeId).pipe(finalize(() => this.isDeleting = false), takeUntilDestroyed(this.destroyRef)).subscribe({
        next: () => {
          this.notifications.success('Profile removed securely.');
          this.loadPaginatedEmployees();
          this.preloadDropdownRelationshipDependencies();
          this.cancelEmployeeWorkspaceEdit();
        },
        error: (err) => console.error('Target resource processing request dropped:', err)
      });
    }
  }
  
  compareEmployeesById(e1: Employee, e2: Employee): boolean {
    if (!e1 || !e2) return false;
    return e1.employeeId === e2.employeeId;
  }
}