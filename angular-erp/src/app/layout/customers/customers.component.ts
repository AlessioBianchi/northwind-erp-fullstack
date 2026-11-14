import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, finalize, Subject, Subscription } from 'rxjs';
import { CustomersService } from '../../service/customers.service';
import { Customer } from './customer.model';
import { LoadingSpinnerComponent } from '../../loading-spinner/loading-spinner.component';
import { ErrorNotificationService } from '../../service/error-notification.service';
import { ConfirmDialogService } from '../../service/confirm-dialog.service';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingSpinnerComponent],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css'
})
export class CustomersComponent implements OnInit {
  private customersService = inject(CustomersService);
  private destroyRef = inject(DestroyRef);
  private notifications = inject(ErrorNotificationService);
  private confirmDialog = inject(ConfirmDialogService);

  currentPage = 1;
  pageSize = 50;
  totalElements = 0;
  totalPages = 0;
  isLoading = false;
  isDeleting = false;

  customersList: Customer[] = [];
  selectedCustomerId: number | null = null;
  currentWorkspaceState: 'empty' | 'edit' | 'new' = 'empty';
  
  activeCustomerForm: Partial<Customer> = {};
  searchQuery = '';
  private searchInput$ = new Subject<string>();
  private listSubscription?: Subscription;

  ngOnInit(): void {
    this.searchInput$.pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(query => {
        this.searchQuery = query;
        this.currentPage = 1;
        this.loadPaginatedCustomers();
      });
    this.loadPaginatedCustomers();
  }

  loadPaginatedCustomers(): void {
    const apiPageIdx = this.currentPage - 1;

    this.listSubscription?.unsubscribe();
    this.isLoading = true;
    this.listSubscription = this.customersService.getPaginatedCustomers(apiPageIdx, this.pageSize, this.searchQuery)
      .pipe(finalize(() => this.isLoading = false), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.customersList = response.content;
          this.totalElements = response.page.totalElements;
          this.totalPages = response.page.totalPages;
        },
        error: (err) => console.error('Error fetching customers:', err)
      });
  }

  onSearchInput(event: Event): void {
    this.searchInput$.next((event.target as HTMLInputElement).value.trim());
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.loadPaginatedCustomers();
  }

  selectCustomer(customer: Customer): void {
    this.selectedCustomerId = customer.customerId ?? null;
    this.currentWorkspaceState = 'edit';
    this.activeCustomerForm = JSON.parse(JSON.stringify(customer));
  }

  initializeNewCustomerForm(): void {
    this.selectedCustomerId = null;
    this.currentWorkspaceState = 'new';
    this.activeCustomerForm = {
      customerCode: '',
      companyName: '',
      contactName: '',
      contactTitle: '',
      address: '',
      city: '',
      postalCode: '',
      region: '',
      country: '',
      phone: '',
      fax: ''
    };
  }

  cancelCustomerEdit(): void {
    this.selectedCustomerId = null;
    this.initializeNewCustomerForm();
  }

  saveCustomerHeader(): void {
    const customer = this.activeCustomerForm as Customer;
    const request$ = customer.customerId
      ? this.customersService.updateCustomer(customer)
      : this.customersService.createCustomer(customer);
    
    request$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
          this.notifications.success('Customer saved successfully.');
          this.loadPaginatedCustomers();
          this.cancelCustomerEdit();
        },
        error: (err) => console.error('Error saving customer:', err)
    });
  }

  async deleteCustomer(): Promise<void> {
    if (!this.selectedCustomerId || this.isDeleting) return;
    if (await this.confirmDialog.confirm('Are you sure you want to delete this customer?')) {
      this.isDeleting = true;
      this.customersService.deleteCustomer(this.selectedCustomerId).pipe(finalize(() => this.isDeleting = false), takeUntilDestroyed(this.destroyRef)).subscribe({
        next: () => {
          this.notifications.success('Customer deleted.');
          this.loadPaginatedCustomers();
          this.cancelCustomerEdit();
        },
        error: (err) => console.error('Error deleting customer:', err)
      });
    }
  }
}