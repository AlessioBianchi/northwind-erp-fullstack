export interface Employee {
  employeeId?: number;
  lastName: string;
  firstName: string;
  title: string;
  titleOfCourtesy?: string;
  birthDate: string;
  hireDate: string;
  address?: string;
  city?: string;
  region?: string;
  postalCode?: string;
  country?: string;
  homePhone?: string;
  extension?: string;
  notes?: string;
  reportsTo?: Employee;
  username?: string;
  password?: string;
}