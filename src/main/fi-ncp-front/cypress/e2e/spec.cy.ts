describe('Visit frontpage', () => {
  it('Visits the initial project page', () => {
    cy.visit('/')
    cy.contains('Suomen kansallinen yhteyspiste')
  })
})
