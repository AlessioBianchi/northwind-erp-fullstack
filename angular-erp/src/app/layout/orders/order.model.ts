import { type Customer } from "../customers/customer.model";
import { type Employee } from "../employees/employee.model";
import { Product } from "../products/product.model";
import { type Shipper } from "../suppliers/shipper.model";

export interface Order {
    orderId: number;
    customer?: Customer;
    employee?: Employee;
    orderDate: string;
    requiredDate: string;
    shippedDate?: string;
    shipper?: Shipper;
    freight: number;
    shipName: string;
    shipAddress: string;
    shipCity: string;
    shipRegion?: string;
    shipPostalCode: string;
    shipCountry: string;
}

export interface OrderDetail {
  unitPrice: number;
  quantity: number;
  discount: number;
  product?: Product; 
  order?: Order;
}