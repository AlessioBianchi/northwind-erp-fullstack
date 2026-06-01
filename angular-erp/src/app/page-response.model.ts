export interface PageResponse<T> {
  content: T[];
  page: {
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
  }
}