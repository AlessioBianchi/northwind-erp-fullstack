import { type Supplier } from "../suppliers/supplier.model";
import { type Category } from "./category.model";

export interface Product {
    productId: number;
    productName: string;
    supplier?: Supplier;
    category?: Category;
    quantityPerUnit: string;
    unitPrice: number;
    unitsInStock: number;
    unitsOnOrder: number;
    reorderLevel: number;
    discontinued: 'Y' | 'N';
}