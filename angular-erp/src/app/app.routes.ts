import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './layout/dashboard/dashboard.component';
import { OrdersComponent } from './layout/orders/orders.component';
import { ProductsComponent } from './layout/products/products.component';
import { CustomersComponent } from './layout/customers/customers.component';
import { SuppliersComponent } from './layout/suppliers/suppliers.component';
import { EmployeesComponent } from './layout/employees/employees.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  //{ path: '**', redirectTo: 'login' },
  {
    path: 'layout',
    component: MainLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'products', component: ProductsComponent },
      { path: 'customers', component: CustomersComponent },
      { path: 'suppliers', component: SuppliersComponent },
      { path: 'employees', component: EmployeesComponent }
    ]
  },
];