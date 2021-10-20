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

describe('Sector e2e test', () => {
  const sectorPageUrl = '/sector';
  const sectorPageUrlPattern = new RegExp('/sector(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const sectorSample = { sectorName: 'Burundi calculating' };

  let sector: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/sectors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sectors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sectors/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (sector) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sectors/${sector.id}`,
      }).then(() => {
        sector = undefined;
      });
    }
  });

  it('Sectors menu should load Sectors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sector');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Sector').should('exist');
    cy.url().should('match', sectorPageUrlPattern);
  });

  describe('Sector page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(sectorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Sector page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/sector/new$'));
        cy.getEntityCreateUpdateHeading('Sector');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', sectorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sectors',
          body: sectorSample,
        }).then(({ body }) => {
          sector = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sectors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [sector],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(sectorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Sector page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('sector');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', sectorPageUrlPattern);
      });

      it('edit button click should load edit Sector page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Sector');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', sectorPageUrlPattern);
      });

      it('last delete button click should delete instance of Sector', () => {
        cy.intercept('GET', '/api/sectors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('sector').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', sectorPageUrlPattern);

        sector = undefined;
      });
    });
  });

  describe('new Sector page', () => {
    beforeEach(() => {
      cy.visit(`${sectorPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Sector');
    });

    it('should create an instance of Sector', () => {
      cy.get(`[data-cy="sectorName"]`).type('infomediaries').should('have.value', 'infomediaries');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        sector = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', sectorPageUrlPattern);
    });
  });
});
