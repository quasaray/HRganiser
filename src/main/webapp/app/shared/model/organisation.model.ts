import { IUser } from 'app/shared/model/user.model';
import { IEmployee } from 'app/shared/model/employee.model';

export interface IOrganisation {
  id?: number;
  orgName?: string;
  staff?: IUser[] | null;
  employees?: IEmployee[] | null;
}

export const defaultValue: Readonly<IOrganisation> = {};
