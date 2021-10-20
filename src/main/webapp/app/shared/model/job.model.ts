import { IEmployee } from 'app/shared/model/employee.model';

export interface IJob {
  id?: number;
  jobTitle?: string;
  employeds?: IEmployee[] | null;
}

export const defaultValue: Readonly<IJob> = {};
