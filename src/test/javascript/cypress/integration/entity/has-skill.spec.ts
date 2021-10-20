import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('HasSkill e2e test', () => {
  const hasSkillPageUrl = '/has-skill';
  const hasSkillPageUrlPattern = new RegExp('/has-skill(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const hasSkillSample = { level: 'Basic' };

  let hasSkill: any;
  let skill: any;
  let employee: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/skills',
      body: { skillName: 'pixel' },
    }).then(({ body }) => {
      skill = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/employees',
      body: {
        firstName: 'Ignacio',
        lastName: 'Welch',
        knownAs: 'Designer',
        email: 'Rowan.Renner@yahoo.com',
        doj: '2021-10-20',
        dod: '2021-10-20',
        dob: '2021-10-19',
        active: true,
        resume: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
        resumeContentType: 'unknown',
        jobLevel: 'Senior',
      },
    }).then(({ body }) => {
      employee = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/has-skills+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/has-skills').as('postEntityRequest');
    cy.intercept('DELETE', '/api/has-skills/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/skills', {
      statusCode: 200,
      body: [skill],
    });

    cy.intercept('GET', '/api/employees', {
      statusCode: 200,
      body: [employee],
    });
  });

  afterEach(() => {
    if (hasSkill) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/has-skills/${hasSkill.id}`,
      }).then(() => {
        hasSkill = undefined;
      });
    }
  });

  afterEach(() => {
    if (skill) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/skills/${skill.id}`,
      }).then(() => {
        skill = undefined;
      });
    }
    if (employee) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/employees/${employee.id}`,
      }).then(() => {
        employee = undefined;
      });
    }
  });

  it('HasSkills menu should load HasSkills page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('has-skill');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('HasSkill').should('exist');
    cy.url().should('match', hasSkillPageUrlPattern);
  });

  describe('HasSkill page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(hasSkillPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create HasSkill page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/has-skill/new$'));
        cy.getEntityCreateUpdateHeading('HasSkill');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', hasSkillPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/has-skills',

          body: {
            ...hasSkillSample,
            skill: skill,
            employee: employee,
          },
        }).then(({ body }) => {
          hasSkill = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/has-skills+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [hasSkill],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(hasSkillPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details HasSkill page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('hasSkill');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', hasSkillPageUrlPattern);
      });

      it('edit button click should load edit HasSkill page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HasSkill');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', hasSkillPageUrlPattern);
      });

      it('last delete button click should delete instance of HasSkill', () => {
        cy.intercept('GET', '/api/has-skills/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('hasSkill').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', hasSkillPageUrlPattern);

        hasSkill = undefined;
      });
    });
  });

  describe('new HasSkill page', () => {
    beforeEach(() => {
      cy.visit(`${hasSkillPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('HasSkill');
    });

    it('should create an instance of HasSkill', () => {
      cy.get(`[data-cy="level"]`).select('Basic');

      cy.get(`[data-cy="skill"]`).select(1);
      cy.get(`[data-cy="employee"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        hasSkill = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', hasSkillPageUrlPattern);
    });
  });
});
