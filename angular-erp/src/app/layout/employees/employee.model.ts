export interface Employee {
  employeeId?: number;
  lastname: string;
  firstname: string;
  title: string;
  titleOfCourtesy?: string;
  birthdate: string;
  hiredate: string;
  address?: string;
  city?: string;
  region?: string;
  postalCode?: string;
  country?: string;
  homePhone?: string;
  extension?: string;
  photo?: string;
  notes?: string;
  reportsTo?: Employee;
  username?: string;
  password?: string;
}