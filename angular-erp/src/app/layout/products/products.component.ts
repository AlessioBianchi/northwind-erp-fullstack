import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Product } from './product.model';
import { Category } from './category.model';
import { ProductsService } from '../../service/products.service'; 
import { SuppliersService } from '../../service/suppliers.service';
import { CategoriesService } from '../../service/categories.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {
  private productsService = inject(ProductsService);
  private suppliersService = inject(SuppliersService);
  private categoriesService = inject(CategoriesService);
  
  currentPage = 1;
  pageSize = 50;
  totalPages = 0;
  totalElements = 0;
  
  selectedProductId: number | null = null;
  currentWorkspaceState: 'empty' | 'edit' | 'new' = 'empty';
  activeTab: 'product' | 'category' = 'product';
  
  productsList: Product[] = [];
  referenceCategoriesList: Category[] = [];
  referenceSuppliersList: any[] = [];

  activeProductForm: Partial<Product> = {};
  activeCategoryForm: Partial<Category> = {};
  selectedCategoryDropdownTarget: Category | undefined = undefined;
  
  searchQuery = '';

  ngOnInit(): void {
    this.loadPaginatedProducts();
    this.preloadDropdownRelationshipDependencies();
  }

  loadPaginatedProducts(): void {
    const apiIndex = this.currentPage - 1;
    
    this.productsService.getPaginatedProducts(apiIndex, this.pageSize).subscribe({
      next: (res) => {
        this.productsList = res.content;
        this.totalElements = res.page.totalElements;
        this.totalPages = res.page.totalPages;
      },
      error: (err) => console.error('Error fetching paginated products:', err)
    });
  }

  preloadDropdownRelationshipDependencies(): void {
    this.categoriesService.getAllCategories().subscribe({
      next: (categories) => this.referenceCategoriesList = categories,
      error: (err) => console.error('Failed to load category dependencies:', err)
    });

    this.suppliersService.getAllSuppliers().subscribe({
      next: (suppliers) => this.referenceSuppliersList = suppliers,
      error: (err) => console.error('Failed to load supplier dependencies:', err)
    });
  }
  
  onSearchInput(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value.toLowerCase();
  }
  
  get filteredProducts() {
    if (!this.searchQuery) return this.productsList;

    return this.productsList.filter(p => {
      const matchesName = p.productName?.toLowerCase().includes(this.searchQuery) ?? false;
      const matchesCategory = p.category?.categoryName?.toLowerCase().includes(this.searchQuery) ?? false;
      const matchesSupplier = p.supplier?.companyName?.toLowerCase().includes(this.searchQuery) ?? false;

      return matchesName || matchesCategory || matchesSupplier;
    });
  }

  onPageChange(targetPage: number): void {
    this.currentPage = targetPage;
    this.loadPaginatedProducts();
  }

  switchTab(target: 'product' | 'category'): void {
    this.activeTab = target;
  }
  
  selectProduct(product: Product): void {
    this.selectedProductId = product.productId ?? null;
    this.currentWorkspaceState = 'edit';
    this.activeTab = 'product';
    this.activeProductForm = JSON.parse(JSON.stringify(product));
  }

  initializeNewProductForm(): void {
    this.selectedProductId = null;
    this.currentWorkspaceState = 'new';
    this.activeTab = 'product';
    this.activeProductForm = {
      productName: '',
      category: undefined,
      supplier: undefined,
      quantityPerUnit: '',
      unitPrice: 0.00,
      unitsInStock: 0,
      unitsOnOrder: 0,
      reorderLevel: 0,
      discontinued: 'N'
    };
  }

  cancelProductWorkspaceEdit(): void {
    this.selectedProductId = null;
    this.initializeNewProductForm();
  }

  saveProductHeader(event: Event): void {
    event.preventDefault(); 
    
    if (this.activeProductForm.productId === null) {
      this.productsService.saveProduct(this.activeProductForm as Product).subscribe({
        next: (savedResult) => {
          alert('Product saved successfully.');
          this.loadPaginatedProducts();
          this.cancelProductWorkspaceEdit();
        },
        error: (err) => console.error('Error saving product:', err)
      });
    } else {
      this.productsService.updateProduct(this.activeProductForm as Product).subscribe({
        next: (savedResult) => {
          alert('Product saved successfully.');
          this.loadPaginatedProducts();
          this.cancelProductWorkspaceEdit();
        },
        error: (err) => console.error('Error saving product:', err)
      });
    }
  }

  deleteActiveProduct(): void {
    if (!this.selectedProductId) return;

    if (confirm('Are you sure you want to delete this product?')) {
      this.productsService.deleteProduct(this.selectedProductId).subscribe({
        next: (response) => {
          alert(response.message || 'Product deleted.');
          this.loadPaginatedProducts();
          this.cancelProductWorkspaceEdit();
        },
        error: (err) => console.error('Error deleting product:', err)
      });
    }
  }
  
  onCategoryDropdownSelectionChange(): void {
    if (this.selectedCategoryDropdownTarget) {
      this.activeCategoryForm = JSON.parse(JSON.stringify(this.selectedCategoryDropdownTarget));
    } else {
      this.activeCategoryForm = { categoryName: '', description: '' };
    }
  }

  cancelCategoryWorkspaceEdit(): void {
    this.selectedCategoryDropdownTarget = undefined;
    this.activeCategoryForm = {};
  }

  saveCategoryHeader(event: Event): void {
    event.preventDefault();

    if (this.activeCategoryForm.categoryId === null) {
      this.categoriesService.saveCategory(this.activeCategoryForm as Category).subscribe({
        next: (savedCat) => {
          alert('Category saved successfully.');
          this.preloadDropdownRelationshipDependencies(); 
          this.cancelCategoryWorkspaceEdit();
          this.loadPaginatedProducts(); 
        },
        error: (err) => console.error('Error saving category:', err)
      });
    } else {
      this.categoriesService.updateCategory(this.activeCategoryForm as Category).subscribe({
        next: (savedCat) => {
          alert('Category saved successfully.');
          this.preloadDropdownRelationshipDependencies(); 
          this.cancelCategoryWorkspaceEdit();
          this.loadPaginatedProducts(); 
        },
        error: (err) => console.error('Error saving category:', err)
      });
    }
  }

  deleteActiveCategory(): void {
    const targetId = this.activeCategoryForm.categoryId;
    if (!targetId) return;

    if (confirm('Are you sure you want to delete this category?')) {
      this.categoriesService.deleteCategory(targetId).subscribe({
        next: (res) => {
          alert(res.message || 'Category deleted.');
          this.preloadDropdownRelationshipDependencies();
          this.cancelCategoryWorkspaceEdit();
          this.loadPaginatedProducts();
        },
        error: (err) => alert('Failed to delete category.')
      });
    }
  }

  compareObjectsById(idPropertyKey: string): (o1: any, o2: any) => boolean {
    return (o1: any, o2: any) => {
      if (!o1 || !o2) return false;
      return o1[idPropertyKey] === o2[idPropertyKey];
    };
  }
}