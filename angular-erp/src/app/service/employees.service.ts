import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee } from '../layout/employees/employee.model';
import { PageResponse } from '../page-response.model';

@Injectable({
    providedIn: 'root'
})
export class EmployeesService {
    private http = inject(HttpClient);
    
    private apiBaseUrl = 'http://localhost:8080/api/v1/employees';
    private httpOptions = { withCredentials: true };
    
    findAllByOrderByEmployeeIdDesc(): Observable<Employee[]> {
        return this.http.get<Employee[]>(this.apiBaseUrl + '/all', this.httpOptions);
    }
    
    findPaginated(pageNumber: number, pageSize: number): Observable<PageResponse<Employee>> {
        const params = new HttpParams()
            .set('pageNumber', pageNumber.toString())
            .set('pageSize', pageSize.toString());

        return this.http.get<PageResponse<Employee>>(this.apiBaseUrl + '/paginated', { 
            ...this.httpOptions, 
            params 
        });
    }
    
    save(employee: Employee): Observable<Employee> {
        return this.http.post<Employee>(this.apiBaseUrl + '/save', employee, this.httpOptions);
    }
    
    delete(employeeId: number): Observable<{ message: string }> {
        return this.http.get<{ message: string }>(this.apiBaseUrl + '/delete/' + employeeId, this.httpOptions);
    }
}