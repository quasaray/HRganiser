import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from 'app/modules/administration/user-management/user-management.reducer';
import register from 'app/modules/account/register/register.reducer';
import activate from 'app/modules/account/activate/activate.reducer';
import password from 'app/modules/account/password/password.reducer';
import settings from 'app/modules/account/settings/settings.reducer';
import passwordReset from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import organisation from 'app/entities/organisation/organisation.reducer';
// prettier-ignore
import location from 'app/entities/location/location.reducer';
// prettier-ignore
import department from 'app/entities/department/department.reducer';
// prettier-ignore
import sector from 'app/entities/sector/sector.reducer';
// prettier-ignore
import employee from 'app/entities/employee/employee.reducer';
// prettier-ignore
import job from 'app/entities/job/job.reducer';
// prettier-ignore
import hasSkill from 'app/entities/has-skill/has-skill.reducer';
// prettier-ignore
import skill from 'app/entities/skill/skill.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  organisation,
  location,
  department,
  sector,
  employee,
  job,
  hasSkill,
  skill,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
