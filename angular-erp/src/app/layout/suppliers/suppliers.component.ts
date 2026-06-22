import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Supplier } from './supplier.model';
import { SuppliersService } from '../../service/suppliers.service';
import { ShippersService } from '../../service/shippers.service';
import { Shipper } from './shipper.model';

@Component({
  selector: 'app-suppliers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './suppliers.component.html',
  styleUrl: './suppliers.component.css'
})
export class SuppliersComponent implements OnInit {
  private suppliersService = inject(SuppliersService);
  private shippersService = inject(ShippersService);
  
  currentPage = 1;
  pageSize = 50;
  totalPages = 0;
  totalElements = 0;
  
  selectedSupplierId: number | null = null;
  currentWorkspaceState: 'empty' | 'edit' | 'new' = 'empty';
  activeTab: 'supplier' | 'shipper' = 'supplier';
  
  suppliersList: Supplier[] = [];
  referenceShippersList: any[] = [];

  activeSupplierForm: Partial<Supplier> = {};
  activeShipperForm: Partial<Shipper> = {};
  selectedShipperDropdownTarget: any = undefined;
  
  searchQuery = '';

  ngOnInit(): void {
    this.loadPaginatedSuppliers();
    this.preloadDropdownRelationshipDependencies();
  }

  loadPaginatedSuppliers(): void {
    const apiIndex = this.currentPage - 1;
    
    this.suppliersService.getPaginatedSuppliers(apiIndex, this.pageSize).subscribe({
      next: (res) => {
        this.suppliersList = res.content;
        this.totalElements = res.page.totalElements;
        this.totalPages = res.page.totalPages;
      },
      error: (err) => console.error('Error fetching paginated suppliers:', err)
    });
  }

  preloadDropdownRelationshipDependencies(): void {
    this.shippersService.getAllShippers().subscribe({
      next: (shippers) => this.referenceShippersList = shippers,
      error: (err) => console.error('Failed to load shipper dependencies:', err)
    });
  }
  
  onSearchInput(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value.toLowerCase();
  }
  
  get filteredSuppliers() {
    if (!this.searchQuery) return this.suppliersList;

    return this.suppliersList.filter(s => {
      const matchesCompany = s.companyName?.toLowerCase().includes(this.searchQuery) ?? false;
      const matchesContact = s.contactName?.toLowerCase().includes(this.searchQuery) ?? false;

      return matchesCompany || matchesContact;
    });
  }

  onPageChange(targetPage: number): void {
    this.currentPage = targetPage;
    this.loadPaginatedSuppliers();
  }

  switchTab(target: 'supplier' | 'shipper'): void {
    this.activeTab = target;
  }
  
  selectSupplier(supplier: Supplier): void {
    this.selectedSupplierId = supplier.supplierId ?? null;
    this.currentWorkspaceState = 'edit';
    this.activeTab = 'supplier';
    this.activeSupplierForm = JSON.parse(JSON.stringify(supplier));
  }

  initializeNewSupplierForm(): void {
    this.selectedSupplierId = null;
    this.currentWorkspaceState = 'new';
    this.activeTab = 'supplier';
    this.activeSupplierForm = {
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

  cancelSupplierWorkspaceEdit(): void {
    this.selectedSupplierId = null;
    this.initializeNewSupplierForm();
  }

  saveSupplierHeader(event: Event): void {
    event.preventDefault();

    const supplier = this.activeSupplierForm as Supplier;
    const request$ = supplier.supplierId
      ? this.suppliersService.updateSupplier(supplier)
      : this.suppliersService.createSupplier(supplier);

    request$.subscribe({
      next: (savedResult) => {
          alert('Supplier saved successfully.');
          this.loadPaginatedSuppliers();
          this.cancelSupplierWorkspaceEdit();
        },
        error: (err) => console.error('Error saving supplier:', err)
    });
  }

  deleteActiveSupplier(): void {
    if (!this.selectedSupplierId) return;

    if (confirm('Are you sure you want to delete this supplier?')) {
      this.suppliersService.deleteSupplier(this.selectedSupplierId).subscribe({
        next: (response) => {
          alert(response.message || 'Supplier deleted.');
          this.loadPaginatedSuppliers();
          this.cancelSupplierWorkspaceEdit();
        },
        error: (err) => console.error('Error deleting supplier:', err)
      });
    }
  }
  
  onShipperDropdownSelectionChange(): void {
    if (this.selectedShipperDropdownTarget) {
      this.activeShipperForm = JSON.parse(JSON.stringify(this.selectedShipperDropdownTarget));
    } else {
      this.activeShipperForm = { companyName: '', phone: '' };
    }
  }

  cancelShipperWorkspaceEdit(): void {
    this.selectedShipperDropdownTarget = undefined;
    this.activeShipperForm = {};
  }

  saveShipperHeader(event: Event): void {
    event.preventDefault();

    const shipper = this.activeShipperForm as Shipper;
    const request$ = shipper.shipperId
      ? this.shippersService.updateShipper(shipper)
      : this.shippersService.createShipper(shipper);
    
    request$.subscribe({
      next: (savedShip) => {
          alert('Shipper saved successfully.');
          this.preloadDropdownRelationshipDependencies(); 
          this.cancelShipperWorkspaceEdit();
          this.loadPaginatedSuppliers(); 
        },
        error: (err) => console.error('Error saving shipper:', err)
    });
  }

  deleteActiveShipper(): void {
    const targetId = this.activeShipperForm.shipperId;
    if (!targetId) return;

    if (confirm('Are you sure you want to delete this shipper?')) {
      this.shippersService.deleteShipper(targetId).subscribe({
        next: (res) => {
          alert(res.message || 'Shipper deleted.');
          this.preloadDropdownRelationshipDependencies();
          this.cancelShipperWorkspaceEdit();
          this.loadPaginatedSuppliers();
        },
        error: (err) => alert('Failed to delete shipper.')
      });
    }
  }
}