import { ISkill } from 'app/shared/model/skill.model';
import { IEmployee } from 'app/shared/model/employee.model';
import { SkillLevel } from 'app/shared/model/enumerations/skill-level.model';

export interface IHasSkill {
  id?: number;
  level?: SkillLevel;
  skill?: ISkill;
  employee?: IEmployee;
}

export const defaultValue: Readonly<IHasSkill> = {};
