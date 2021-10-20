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

describe('Organisation e2e test', () => {
  const organisationPageUrl = '/organisation';
  const organisationPageUrlPattern = new RegExp('/organisation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const organisationSample = { orgName: 'Clothing' };

  let organisation: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/organisations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/organisations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/organisations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (organisation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/organisations/${organisation.id}`,
      }).then(() => {
        organisation = undefined;
      });
    }
  });

  it('Organisations menu should load Organisations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('organisation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Organisation').should('exist');
    cy.url().should('match', organisationPageUrlPattern);
  });

  describe('Organisation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(organisationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Organisation page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/organisation/new$'));
        cy.getEntityCreateUpdateHeading('Organisation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/organisations',
          body: organisationSample,
        }).then(({ body }) => {
          organisation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/organisations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [organisation],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(organisationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Organisation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('organisation');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);
      });

      it('edit button click should load edit Organisation page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Organisation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);
      });

      it('last delete button click should delete instance of Organisation', () => {
        cy.intercept('GET', '/api/organisations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('organisation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', organisationPageUrlPattern);

        organisation = undefined;
      });
    });
  });

  describe('new Organisation page', () => {
    beforeEach(() => {
      cy.visit(`${organisationPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Organisation');
    });

    it('should create an instance of Organisation', () => {
      cy.get(`[data-cy="orgName"]`).type('France').should('have.value', 'France');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        organisation = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', organisationPageUrlPattern);
    });
  });
});
