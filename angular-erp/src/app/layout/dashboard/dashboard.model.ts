import { type Order } from "../orders/order.model";
import { Product } from "../products/product.model";

export interface DashboardData {
    totalOrdersCount: number;
    totalRevenue: number;
    lastMonthOrdersCount: number;
    lastMonthRevenue: number;
    lastTenOrders: Order[];
    ordersByCategory: Record<string, number>;
    productStock: Product[];
}