import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Order, OrderDetail } from './order.model';
import { Customer } from '../customers/customer.model';
import { Shipper } from '../suppliers/shipper.model';
import { OrdersService } from '../../service/orders.service';
import { CustomersService } from '../../service/customers.service';
import { ShippersService } from '../../service/shippers.service';
import { Product } from '../products/product.model';
import { ProductsService } from '../../service/products.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit{
  private ordersService = inject(OrdersService);
  private customersService = inject(CustomersService);
  private shippersService = inject(ShippersService);
  private productsService = inject(ProductsService);
  
  currentPage = 1; 
  pageSize = 50;
  totalElements = 0;
  totalPages = 0;

  selectedOrderId: number | null = null;
  selectedDetail: OrderDetail | null = null;
  currentWorkspaceState: 'empty' | 'edit' | 'new' = 'empty';
  activeTab: 'form' | 'details' = 'form';
  isDetailModalOpen = false;

  badgeText = 'Select an order';
  badgeClass = 'bg-info text-white';

  ordersList: Order[] = [];
  orderDetailsList: OrderDetail[] = [];
  customersList: Customer[] = []; 
  shippersList: Shipper[] = [];
  productsList: Product[] = [];

  activeForm: Partial<Order> = {};
  modalDetailForm: OrderDetail = {
    product: undefined,
    order: undefined,
    unitPrice: 0.00,
    quantity: 1,
    discount: 0.00
  };

  ngOnInit(): void {
    this.loadPaginatedOrders();
  }

  loadPaginatedOrders(): void {
    const apiPageIdx = this.currentPage - 1;

    this.ordersService.getPaginatedOrders(apiPageIdx, this.pageSize).subscribe({
      next: (response) => {
        this.ordersList = response.content;
        this.totalElements = response.page.totalElements;
        this.totalPages = response.page.totalPages;
      },
      error: (err) => console.error('Error fetching paginated orders:', err)
    });
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.loadPaginatedOrders();
  }

  selectOrder(order: Order): void {
    this.selectedDetail = null;
    this.selectedOrderId = order.orderId;
    this.currentWorkspaceState = 'edit';
    this.activeTab = 'form';
    this.badgeText = `Order #${order.orderId}`;
    this.badgeClass = 'bg-primary text-white';
    this.activeForm = JSON.parse(JSON.stringify(order));
    
    this.loadCustomersList();
    this.loadShippersList();
    this.loadOrderDetailsList(order.orderId);
  }

  saveActiveOrder(event?: Event): void {
    if (event) event.preventDefault();

    if (this.activeForm.orderId === null) {
      this.ordersService.saveOrder(this.activeForm as Order).subscribe({
        next: (savedOrder) => {
          alert('Order saved successfully!');
          this.loadPaginatedOrders();
          this.cancelWorkspaceEdit();
        },
        error: (err) => {
          console.error('Error saving order properties:', err);
          alert('Failed to save order changes.');
        }
      });
    } else {
      this.ordersService.updateOrder(this.activeForm as Order).subscribe({
        next: (savedOrder) => {
          alert('Order saved successfully!');
          this.loadPaginatedOrders();
          this.cancelWorkspaceEdit();
        },
        error: (err) => {
          console.error('Error saving order properties:', err);
          alert('Failed to save order changes.');
        }
      });
    }
  }

  deleteActiveOrder(): void {
    if (!this.selectedOrderId) return;
    
    if (confirm('Are you sure you want to drop this order record?')) {
      this.ordersService.deleteOrder(this.selectedOrderId).subscribe({
        next: (res) => {
          alert(res.message);
          this.loadPaginatedOrders();
          this.cancelWorkspaceEdit();
        },
        error: (err) => console.error('Deletion error occurred:', err)
      });
    }
  }

  initializeNewOrderForm(): void {
    this.selectedOrderId = null;
    this.currentWorkspaceState = 'new';
    this.activeTab = 'form';
    this.badgeText = 'New Order Creation';
    this.badgeClass = 'bg-success text-white';
    
    this.activeForm = {
      orderDate: new Date().toISOString().substring(0, 10),
      requiredDate: '',
      shippedDate: '',
      freight: 0,
      shipAddress: '',
      shipCity: '',
      shipRegion: '',
      shipPostalCode: '',
      shipCountry: '',
      customer: undefined,
      shipper: undefined
    };

    this.loadCustomersList();
    this.loadShippersList();
  }
  
  cancelWorkspaceEdit(): void {
    this.selectedOrderId = null;
    this.currentWorkspaceState = 'empty';
    this.activeTab = 'form';
    this.badgeText = 'Select an order';
    this.badgeClass = 'bg-info text-white';
    this.activeForm = {};
    this.orderDetailsList = [];
  }

  switchTab(targetTab: 'form' | 'details'): void {
    this.activeTab = targetTab;
  }

  compareCustomers(customerInDropdown: Customer, customerInSelectedOrder: Customer): boolean {
    if (!customerInDropdown || !customerInSelectedOrder) return false;
    return customerInDropdown.customerId === customerInSelectedOrder.customerId;
  }

  compareShippers(shipperInDropdown: Shipper, shipperInSelectedOrder: Shipper): boolean {
    if (!shipperInDropdown || !shipperInSelectedOrder) return false;
    return shipperInDropdown.shipperId === shipperInSelectedOrder.shipperId;
  }

  selectDetail(detail: OrderDetail) {
    this.selectedDetail = this.selectedDetail === null ? detail : null;
  }

  addDetail() {
    if (!this.selectedOrderId) return;

    this.modalDetailForm.order = {
      orderId: this.selectedOrderId,
      customer: undefined,
      employee: undefined,
      orderDate: '',
      requiredDate: '',
      shippedDate: '',
      shipper: undefined,
      freight: 0,
      shipName: '',
      shipAddress: '',
      shipCity: '',
      shipRegion: '',
      shipPostalCode: '',
      shipCountry: ''
    };

    this.productsService.getAllProducts().subscribe({
      next: (products) => this.productsList = products,
      error: (err) => console.log('Error fetching products list', err)
    });

    this.isDetailModalOpen = true;
  }

  closeDetailModal(): void {
    this.isDetailModalOpen = false;
  }

  onProductSelectChange(): void {
    if (this.modalDetailForm.product) {
      this.modalDetailForm.unitPrice = this.modalDetailForm.product.unitPrice || 0;
    }
  }

  updateDiscountFraction(percentValue: number): void {
    this.modalDetailForm.discount = percentValue / 100;
  }

  submitDetailForm(): void {
    if (!this.modalDetailForm.product || !this.modalDetailForm.quantity) return;
    
    // TODO: check this, I'm not sure about the condition
    if (this.modalDetailForm.order === null && this.modalDetailForm.product === null) {
      this.ordersService.saveOrderDetail(this.modalDetailForm as OrderDetail).subscribe({
        next: (savedDetail) => {
          alert('Order detail saved successfully!');
          this.loadPaginatedOrders();
          this.cancelWorkspaceEdit();
        },
        error: (err) => {
          console.error('Error saving order detail properties:', err);
          alert('Failed to save order detail.');
        }
      });
    } else {
      this.ordersService.updateOrderDetail(this.modalDetailForm as OrderDetail).subscribe({
        next: (savedDetail) => {
          alert('Order detail saved successfully!');
          this.loadPaginatedOrders();
          this.cancelWorkspaceEdit();
        },
        error: (err) => {
          console.error('Error saving order detail properties:', err);
          alert('Failed to save order detail.');
        }
      });
    }

    this.closeDetailModal();
  }

  deleteActiveDetail() {
    if (!this.selectedDetail) return;

    if (confirm('Are you sure you want to drop this order detail record?')) {
      this.ordersService.deleteOrderDetail(this.selectedDetail).subscribe({
        next: (res) => {
          alert(res.message);
          this.loadPaginatedOrders();
          this.cancelWorkspaceEdit();
        },
        error: (err) => console.error('Deletion error occurred:', err)
      });
    }
  }

  loadCustomersList() {
    this.customersService.getAllCustomers().subscribe({
      next: (customers) => this.customersList = customers,
      error: (err) => console.log('Error fetching customers list', err)
    });
  }

  loadShippersList() {
    this.shippersService.getAllShippers().subscribe({
      next: (shippers) => this.shippersList = shippers,
      error: (err) => console.log('Error fetching shippers list', err)
    });
  }

  loadOrderDetailsList(orderId: number) {
    this.ordersService.getOrderDetails(orderId).subscribe({
      next: (details) => this.orderDetailsList = details,
      error: (err) => console.error('Error fetching order details:', err)
    });
  }
}