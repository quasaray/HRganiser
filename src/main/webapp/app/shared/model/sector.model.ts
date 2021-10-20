import { IEmployee } from 'app/shared/model/employee.model';
import { IDepartment } from 'app/shared/model/department.model';

export interface ISector {
  id?: number;
  sectorName?: string;
  employees?: IEmployee[] | null;
  departments?: IDepartment[] | null;
}

export const defaultValue: Readonly<ISector> = {};
