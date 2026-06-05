import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomersService } from '../../service/customers.service';
import { Customer } from './customer.model';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css'
})
export class CustomersComponent implements OnInit {
  private customersService = inject(CustomersService);

  currentPage = 1; 
  pageSize = 50;
  totalElements = 0;
  totalPages = 0;

  customersList: Customer[] = [];
  selectedCustomerId: number | null = null;
  currentWorkspaceState: 'empty' | 'edit' | 'new' = 'empty';
  
  activeCustomerForm: Partial<Customer> = {};
  searchQuery = '';

  ngOnInit(): void {
    this.loadPaginatedCustomers();
  }

  loadPaginatedCustomers(): void {
    const apiPageIdx = this.currentPage - 1;

    this.customersService.getPaginatedCustomers(apiPageIdx, this.pageSize).subscribe({
      next: (response) => {
        this.customersList = response.content;
        this.totalElements = response.page.totalElements;
        this.totalPages = response.page.totalPages;
      },
      error: (err) => console.error('Error fetching customers:', err)
    });
  }

  onSearchInput(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value.toLowerCase().trim();
  }
  
  get filteredCustomers(): Customer[] {
    if (!this.searchQuery) return this.customersList;

    return this.customersList.filter(c => {
      const matchesCode = c.customerCode?.toLowerCase().includes(this.searchQuery) ?? false;
      const matchesCompany = c.companyName?.toLowerCase().includes(this.searchQuery) ?? false;

      return matchesCode || matchesCompany;
    });
  }

  onPageChange(direction: number): void {
    this.currentPage += direction;
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
    this.customersService.saveCustomer(this.activeCustomerForm as Customer).subscribe({
      next: () => {
        alert('Customer saved successfully.');
        this.loadPaginatedCustomers();
        this.cancelCustomerEdit();
      },
      error: (err) => console.error('Error saving customer:', err)
    });
  }

  deleteCustomer(): void {
    if (!this.selectedCustomerId) return;
    if (confirm('Are you sure you want to delete this customer?')) {
      this.customersService.deleteCustomer(this.selectedCustomerId).subscribe({
        next: () => {
          alert('Customer deleted.');
          this.loadPaginatedCustomers();
          this.cancelCustomerEdit();
        },
        error: (err) => console.error('Error deleting customer:', err)
      });
    }
  }
}