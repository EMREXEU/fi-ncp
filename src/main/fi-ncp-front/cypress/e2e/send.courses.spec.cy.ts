describe('Send courses', () => {
  // Note that this test will fail if user is not authenticated
  // You can authenticate in fi.csc.emrex.ncp.controller.NcpUiController.getCourses and use test learnerid
  it('Can send one course to preview', () => {
    cy.visit('/');
    // Select first course
    cy.get('.partOfDegree').first().click();
    // Send results to preview
    cy.get('#courses-selected button').click();
    // Verify preview page content
    cy.contains('Henkilötiedot');
    cy.contains('Lähetettävät suoritukset');
    cy.contains('Lähetä');
    cy.contains('Arvosana');
    cy.get('#consent').click();
    cy.get('button').contains('Lähetä').click();
    // After send is not simulated, it is normal to see error page but test passes
  })
})
