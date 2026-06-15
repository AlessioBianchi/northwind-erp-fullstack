import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Employee } from './employee.model';
import { EmployeesService } from '../../service/employees.service';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css'
})
export class EmployeesComponent implements OnInit {
  private employeesService = inject(EmployeesService);
  
  currentPage = 1;
  pageSize = 50;
  totalPages = 0;
  totalElements = 0;
  
  selectedEmployeeId: number | null = null;
  currentWorkspaceState: 'empty' | 'edit' | 'new' = 'empty';
  
  badgeText = '';
  badgeClass = 'd-none';
  
  employeesList: Employee[] = [];
  allEmployeesLookupList: Employee[] = [];
  
  activeEmployeeForm: Partial<Employee> = {};
  searchQuery = '';

  ngOnInit(): void {
    this.loadPaginatedEmployees();
    this.preloadDropdownRelationshipDependencies();
  }

  loadPaginatedEmployees(): void {
    const apiIndex = this.currentPage - 1;
    this.employeesService.getPaginatedEmployees(apiIndex, this.pageSize).subscribe({
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
    this.employeesService.getAllByOrderByEmployeeIdDesc().subscribe({
      next: (data) => this.allEmployeesLookupList = data,
      error: (err) => console.error('Error syncing management roster dictionary listings:', err)
    });
  }

  onSearchInput(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value.toLowerCase();
  }

  get filteredEmployees() {
    if (!this.searchQuery) return this.employeesList;
    return this.employeesList.filter(e => 
      e.firstname.toLowerCase().includes(this.searchQuery) ||
      e.lastname.toLowerCase().includes(this.searchQuery) ||
      e.title.toLowerCase().includes(this.searchQuery)
    );
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.loadPaginatedEmployees();
  }
  
  selectEmployee(employee: Employee): void {
    this.selectedEmployeeId = employee.employeeId ?? null;
    this.currentWorkspaceState = 'edit';
    
    this.badgeClass = 'badge bg-primary text-white';
    
    this.activeEmployeeForm = JSON.parse(JSON.stringify(employee));
  }

  initializeNewEmployeeForm(): void {
    this.selectedEmployeeId = null;
    this.currentWorkspaceState = 'new';
    
    this.badgeText = 'New Profile Template';
    this.badgeClass = 'badge bg-success text-white';

    this.activeEmployeeForm = {
      firstname: '',
      lastname: '',
      title: '',
      birthdate: '',
      hiredate: new Date().toISOString().substring(0, 10),
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

    if (this.activeEmployeeForm.employeeId === null) {
      this.employeesService.save(this.activeEmployeeForm as Employee).subscribe({
        next: (savedResult) => {
          alert('Employee profile record updated successfully.');
          this.loadPaginatedEmployees();
          this.preloadDropdownRelationshipDependencies();
          this.cancelEmployeeWorkspaceEdit();
        },
        error: (err) => console.error('Error processing server authentication profile updates:', err)
      });
    } else {
      this.employeesService.update(this.activeEmployeeForm as Employee).subscribe({
        next: (savedResult) => {
          alert('Employee profile record updated successfully.');
          this.loadPaginatedEmployees();
          this.preloadDropdownRelationshipDependencies();
          this.cancelEmployeeWorkspaceEdit();
        },
        error: (err) => console.error('Error processing server authentication profile updates:', err)
      });
    }
  }

  deleteActiveEmployee(): void {
    if (!this.selectedEmployeeId) return;

    if (confirm('Are you sure you want to drop this worker profile history record?')) {
      this.employeesService.delete(this.selectedEmployeeId).subscribe({
        next: (res) => {
          alert(res.message || 'Profile removed securely.');
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