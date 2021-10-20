import { IHasSkill } from 'app/shared/model/has-skill.model';

export interface ISkill {
  id?: number;
  skillName?: string;
  users?: IHasSkill[] | null;
}

export const defaultValue: Readonly<ISkill> = {};
