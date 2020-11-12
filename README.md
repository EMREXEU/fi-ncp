# fi-ncp
CSC National Contact Point application (NCP) for EMREX.

## Requirements
- maven 3+ 
- java version 8

## Compiling
`mvn clean install`

## Running
`java -jar ./target/ncp-0.0.1-SNAPSHOT.war`

## Requirements

## Authentication
See: `./src/main/resources/application.yml`

## Configuration

## Documentation
https://emrex.eu/technical/

## Coding conventions
- Google style 
- https://google.github.io/styleguide/
- packages by layers
## Sequence
Plant UML can be viewed in  http://www.plantuml.com/plantuml/uml/
- [PlantUML model](./sequence_student_fetches_data_from_virta.puml)

## Running 
- fi-ncp is running on `localhost:9001` on default
- fi-smp demo application is running on `localhost:9002` on default
- fi-smp provides example SMP-web client on URL: `http://localhost:9002/#/`
- fi-smp will contact running fi-ncp when selecting Finland as contact country
