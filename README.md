# fi-ncp
CSC EMP Emrex Data Provider application (EMP) aka EMREX Data Access Point for EMREX. Previously known as NCP (National contact point).
This is the point that the EMREX client contacts to fetch results.

Additional documentation: https://wiki.eduuni.fi/x/LsZnCw, https://github.com/emrex-eu


EMREX guide: https://emrex.eu/wp-content/uploads/2020/01/Technical-Guide-to-EMREX.pdf

## Requirements
- Maven 3+ (tested with 3.9.1)
- Java 17
- Spring 3+
  - Jakarta EE 9 or 10 compatible
- Apache CXF
    - Jakarta EE 9 compatible only
    - cxf-codegen-plugin: wsdl2java
    - cxf-xjc-plugin: xsdtojava
- Angular: 16.1.5
  - Angular CLI: 16.1.5
  - Node: 16.14.2 (npm 8.5.0)
- Optional Build Tools
  - optional: docker, docker-compose


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
  - building front-end will output files to `webapp` folder, and files are included in Spring Boot Jar.
    - Normally executable should be jar but in this case war, because it's the simplest way to include Angular app inside Spring Boot app.

## Compiling
Maven project builds and packages both spring-boot back-end and angular front-end application
into single war.

`mvn clean install -Pdev`

where -P is environment (defaults to dev without -P, -Pdev, -Ptest,-Pproduction)

### Frontend
You can build frontend seperatly when needed ex. sourcemaps with `ng build -c development` see package.json scripts for more examples. `angular.json` has configurations for angular app. For develop mode for now you need to modify pom.xml ng build section and remove omit-dev and change prodcution -> development. This should be automated in the future.

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

### Option 2: Run application behind Apache reverse proxy
If you want to simulate test or production env locally.
> Note: If you need to simulate full production env locally you should consider using test server.
1) Install Apache
2) Create new host entry /etc/hosts ex. `127.0.0.1 local.emrex.com`
3) Create new Apache virtual host configuration ex.
```
<VirtualHost *:8080>
    ServerAdmin webmaster@local.emrex.com
    ServerName local.emrex.com
    ErrorLog "/opt/homebrew/var/log/httpd/local.emrex.com-error_log"
    CustomLog "/opt/homebrew/var/log/httpd/local.emrex.com-access_log" common

    <Location />
        RewriteEngine on
        RewriteCond %{REQUEST_URI} ^/$
        RewriteRule (.*) /fi-ncp/ [R=301]
    </Location>

    <Location /fi-ncp>
        ProxyPreserveHost On
        ProxyPass "http://localhost:9001"
        ProxyPassReverse "http://localhost:9001"
    </Location>

     <Directory /fi-ncp>
        RewriteEngine on

        # Don't rewrite files or directories
        RewriteCond %{REQUEST_FILENAME} -f [OR]
        RewriteCond %{REQUEST_FILENAME} -d
        RewriteRule ^ - [L]

        # Rewrite everything else to index.html to allow html5 state links
        RewriteRule ^ index.html [L]
    </Directory>

</VirtualHost>
```
> This basic reverse proxy configuration routes http traffic to Spring & Angular application, if you need more complex configuration, you should consult real server configuration.
4) Open in browser: `local.emrex.com:8080`

Now your Spring Boot/Angular application is running behind Apache Reverse Proxy.

### Option 3: Run back-end and front-end separately
With the 2 steps from Option 1 done and the backend running
3) Serve front-end separately in `src/main/fi-ncp-front` in your IDE or shell:
   - `cd src/main/fi-ncp-front`
   - `ng serve`

fi-ncp is running on `localhost:9001` but the front-end is also running locally in
`localhost:4200`
Access application root from angular project in your web browser directly: http://localhost:4200

Editing angular code will compile and use hot deploy normally as with `ng serve`. It is
recommended to use private browsing in browser so no javascript code is cached between reloads.
> Warning this is not recommended configuration, Spring Boot application includes Angular Application and if you `ng serve` there may be problems with Angular router because you will have two Angular instances running one from inside Spring Boot application and another from `ng serve`.

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
- docker-compose is not currently tested but may still work

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

## Frontend Error Logging Documentation

### Overview

This document outlines the process for sending frontend error logs to the backend server, ensuring that only relevant error data is transmitted, encrypted, and securely logged.

### Process Description

#### Error Detection:
The frontend application captures errors occurring during user interactions. These errors can include JavaScript exceptions, network failures, and other client-side issues.

####    Log Collection:
Only the relevant error information is collected. This includes the error message, stack trace, and any additional context necessary for debugging.

####    Transmission:
The frontend logs are sent to the backend server over a secure channel (HTTPS). This adds an additional layer of security by protecting the data from interception and unauthorized access.

####    Encryption and Logging:
Upon receipt, the backend server encrypts the log data. The encrypted logs are then processed and stored in a secure logging system for further analysis and monitoring.
