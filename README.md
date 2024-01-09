>This branch is using old ELMO schema/codebase and has been deprecated.

# fi-ncp
CSC National Contact Point application (NCP) for EMREX.

Additional documentation: https://wiki.eduuni.fi/x/LsZnCw

EMREX guide: https://emrex.eu/wp-content/uploads/2020/01/Technical-Guide-to-EMREX.pdf

## Requirements
- maven 3+
- java version 11
- optional: docker, docker-compose
- angular version 11
- npm

The dev env expects to find cert and private key for signing the xml documents as follows:
path.certificate=certs/ncp_dev_cert.cer
ncp.path.encryption.key=certs/ncp.dev.key

You can generate your own with openssl:

`openssl genrsa -out private-key.pem 3072`

`openssl pkcs8 -topk8 -nocrypt -in private-key.pem -out ncp.dev.key`

`openssl rsa -in private-key.pem -pubout -out public-key.pem`

`openssl req -new -x509 -key private-key.pem -out ncp_dev_emrex_cert.cer -days 360`


## Project structure
- Project root contains `./pom.xml` which builds and packages both front-end and back-end
- front-end is a separate folder inside project which can be run also locally as normal angular
  project:
  - `fi-ncp/src/main/fi-ncp-front`

## Compiling
Maven project builds and packages both spring-boot back-end and angular front-end application
into single war.

`mvn clean install -Pdev`

where -P is environment (defaults to dev without -P, -Pdev, -Ptest,-Pproduction)

## Running

#### Java:
`java -jar ./target/fi-ncp-1.0.RELEASE.war`

#### Docker:
See script `run_docker.sh`

#### Docker-compose:
See project https://github.com/EMREXEU/fi-ncp-docker

### Option 1: Using application locally from war
1) Compile all: `mvn clean install -Pdev`
2) Execute `fi.csc.emrex.ncp.FiNcpApplication` on your IDE of choice or run war: `java -jar ./target/fi-ncp-1.0.RELEASE.war`

fi-ncp is running on `localhost:9001` on default

Access front-end from war with web browser: http://localhost:9001

NOTE: front-end and back-end locally still uses configured VIRTA-TEST service.

### Option 2: Run back-end and front-end separately
With the 2 steps from Option 1 done and the backend running
3) Serve front-end separately in `src/main/fi-ncp-front` in your IDE or shell:
   - `cd src/main/fi-ncp-front`
   - `ng serve`

fi-ncp is running on `localhost:9001` but the front-end is also running locally in
`localhost:4200`
Access application root from angular project in your web browser directly: http://localhost:4200

Editing angular code will compile and use hot deploy normally as with `ng serve`. It is
recommended to use private browsing in browser so no javascript code is cached between reloads.

## Authentication
Back-end and front-end do not provide authentication but rely on production configuration where both
are fully protected by shibboleth SSO. Back-end expects Server Variables (https://wiki.shibboleth.net/confluence/display/SP3/AttributeAccess) provided by
shibboleth authentication. When running locally without shibboleth, you can simply input the test ssn or learnedId into the code and restart backend for quick and dirty testing

## Configuration

### back-end
 - `src/main/resources/application.properties`
     - `src/main/resources/application-dev.properties`
     - `src/main/resources/application-test.properties`
     - `src/main/resources/application-production.properties`
 - `src/main/resources/data/issuers.txt`

### docker
- docker-compose

## Documentation
- https://emrex.eu/technical/
- https://emrex.eu/wp-content/uploads/2020/01/Technical-Guide-to-EMREX.pdf
- https://github.com/emrex-eu/elmo-schemas

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
