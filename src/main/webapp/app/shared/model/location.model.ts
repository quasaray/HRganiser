import { IEmployee } from 'app/shared/model/employee.model';

export interface ILocation {
  id?: number;
  locName?: string;
  employees?: IEmployee[] | null;
}

export const defaultValue: Readonly<ILocation> = {};
