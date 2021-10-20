import dayjs from 'dayjs';
import { IHasSkill } from 'app/shared/model/has-skill.model';
import { IOrganisation } from 'app/shared/model/organisation.model';
import { ILocation } from 'app/shared/model/location.model';
import { IDepartment } from 'app/shared/model/department.model';
import { ISector } from 'app/shared/model/sector.model';
import { IJob } from 'app/shared/model/job.model';
import { JobLevel } from 'app/shared/model/enumerations/job-level.model';

export interface IEmployee {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  knownAs?: string | null;
  email?: string | null;
  doj?: string | null;
  dod?: string | null;
  dob?: string | null;
  active?: boolean | null;
  resumeContentType?: string | null;
  resume?: string | null;
  jobLevel?: JobLevel | null;
  skills?: IHasSkill[] | null;
  manager?: IEmployee | null;
  org?: IOrganisation | null;
  loc?: ILocation | null;
  dept?: IDepartment | null;
  sect?: ISector | null;
  job?: IJob | null;
}

export const defaultValue: Readonly<IEmployee> = {
  active: false,
};
