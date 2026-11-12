export interface Order {
    orderId: number;
    customerId?: number;
    customerName?: string;
    employeeId?: number;
    employeeName?: string;
    orderDate: string;
    requiredDate: string;
    shippedDate?: string;
    shipperId?: number;
    shipperName?: string;
    freight: number;
    shipName: string;
    shipAddress: string;
    shipCity: string;
    shipRegion?: string;
    shipPostalCode: string;
    shipCountry: string;
}

export interface OrderDetail {
  orderId?: number;
  productId?: number;
  productName?: string;
  unitPrice: number;
  quantity: number;
  discount: number;
}
