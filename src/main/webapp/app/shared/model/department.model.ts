import { ISector } from 'app/shared/model/sector.model';
import { IEmployee } from 'app/shared/model/employee.model';

export interface IDepartment {
  id?: number;
  departmentName?: string;
  belongsTo?: ISector | null;
  employees?: IEmployee[] | null;
}

export const defaultValue: Readonly<IDepartment> = {};
