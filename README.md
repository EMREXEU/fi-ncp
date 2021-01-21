# fi-ncp
CSC National Contact Point application (NCP) for EMREX.

## API documentation
API provided by NPC is described here:<br> 
https://confluence.csc.fi/display/EMREX/Implementation+details%3A+NCP

EMREX guide: https://emrex.eu/wp-content/uploads/2020/01/Technical-Guide-to-EMREX.pdf

## Requirements
- maven 3+ 
- java version 11
- optional: docker, docker-compose
- angular version 11
- npm

## Project structure
- Project root contains `pom.xml` which builds and packages both front-end and back-end 
- front-end is a separate folder inside project which can be run also locally as normal angular 
  project:`fi-ncp/src/main/fi-ncp-front`
  -  


## Compiling
Maven project builds and packages both spring-boot back-end and angular front-end application 
into single war.
`mvn clean install`


## Running

#### Java:
`java -jar ./target/ncp-0.0.1-SNAPSHOT.war`

#### Docker: 
See script `run_docker.sh`

#### Docker-compose: 
See project https://github.com/EMREXEU/fi-ncp-docker

### Option 1: Using application locally from war
Run:
1) Compile all: `mvn clean install`
2) Run war: `java -jar ./target/ncp-0.0.1-SNAPSHOT.war`

fi-ncp is running on `localhost:9001` on default

1) Mock shibboleth authentication: http://localhost:9001/test/mock_shibbolet_auth/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536 
2) Access front-end from war with web browser: http://localhost:9001/ng/index.html
   
NOTE: front-end and back-end locally still uses configured VIRTA-TEST service.

### Option 2: Run back-end and front-end separately
Run:
1) Compile all: `mvn clean install` (you're only using the back-end from port 9001)
2) Run war: `java -jar ./target/ncp-0.0.1-SNAPSHOT.war` or execute `fi.csc.emrex.ncp.FiNcpApplication` 
   on your IDE of choice.
3) Compile and serve front-end separately in `src/main/fi-ncp-front` in your IDE or shell:
   - `cd src/main/fi-ncp-front`
   - `ng build`
   - `bg serve`

fi-ncp is running on `localhost:9001` but the front-end is also running locally in 
`localhost:4200` 
1) Mock shibboleth authentication: http://localhost:9001/test/mock_shibbolet_auth/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536
2) Access application root from angular project in your web browser directly: http://localhost:4200/ng/index.html
3) Editing angular code will compile and use hot deploy normally as with `ng serve`

## Authentication
Back-end and front-end do not provide authentication but rely on production configuration where both
are fully protected by shibboleth SSO. Back-end expects session/request parameters provided by
shibboleth authentication. When running locally without shibboleth, back-end TestController provides 
end-point for mocking shibboleth authentication.  

## Configuration
### front-end


### back-end
- `src/main/resources/application.properties`
- `src/main/resources/application.yml`

### docker
- docker-compose

## Documentation
https://emrex.eu/technical/

## Coding conventions
- Google style 
- https://google.github.io/styleguide/
- packages by layers

## Sequence
NOTE: only use case `External student fetches courses from VIRTA via fi-ncp` is currently supported.

Plant UML can be viewed in  http://www.plantuml.com/plantuml/uml/

- Use case: External student fetches courses from VIRTA via fi-ncp.
 [PlantUML model](./sequence_student_fetches_courses_from_virta.puml)
- Use case: Internal student imports external courses to VIRTA via fi-ncp.
 [PlantUML model](./sequence_student_imports_external_courses_to_virta.puml)
- internal/external is from fi-ncp point of view
