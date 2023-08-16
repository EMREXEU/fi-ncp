describe('Visit frontpage', () => {
  it('Visits the initial project page', () => {
    cy.visit('/')
    // Title
    cy.contains('Suomen kansallinen yhteyspiste')
    // Send button
    cy.contains('Lähetä valitut suoritukset')

    // Table
    cy.contains('Nimi')
    cy.contains('Laajuus')
    cy.contains('Organisaatio')
    cy.contains('Arvosana')
    cy.contains('Suorituspäivämäärä')
  })
})
