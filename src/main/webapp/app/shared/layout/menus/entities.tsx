import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/organisation">
      <Translate contentKey="global.menu.entities.organisation" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/location">
      <Translate contentKey="global.menu.entities.location" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/department">
      <Translate contentKey="global.menu.entities.department" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/sector">
      <Translate contentKey="global.menu.entities.sector" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/employee">
      <Translate contentKey="global.menu.entities.employee" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/job">
      <Translate contentKey="global.menu.entities.job" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/has-skill">
      <Translate contentKey="global.menu.entities.hasSkill" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/skill">
      <Translate contentKey="global.menu.entities.skill" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
