# Dat250Project

[![FeedApp Tests](../../actions/workflows/gradle.yml/badge.svg)](../../actions/workflows/gradle.yml)

This repo contains a Poll-system application, that lets users create polls and vote on polls made by other users.  

Link to front end of the application: https://github.com/MikalDr/Dat250project-frontend  

## Deploying the application:
[![FeedApp Image build](../../actions/workflows/main.yml/badge.svg)](../../actions/workflows/main.yml)

This application is deployable as a docker container. 
The docker image for the backend of the project can be found following this link:
https://hub.docker.com/layers/mikaldr/dat250-project/latest/images/sha256-d272d2cd84f5c1bc056f3f0cfa49dbaccaf4415dc1fde1a50a4819182c6a4a1d?context=repo

#### to run the application:
1. Pull the latest image using: `docker pull mikaldr/dat250-project:latest`
2. Run the image using `docker run -d -p 8080:8080 mikaldr/dat250-project:latest`
   This will open the port 8080 on your local-machine to the container.

## Running the application in a test environment
When running the application in a test environment, one can run the application using
their preferred IDE, which has support for Gradle projects.

We used IntelliJ for this, but you may use Eclipse if preferred.

---
Below is some of the diagrams created during the planning stage of the project:
### Application flow diagram
![Applicataion Flow Diagram](Diagrams/application_flow.png)
### Architectural diagram
![AoT](Diagrams/ArchitecturalDiagram.png)
### Database diagram
![Database Diagram](Diagrams/Database.png)
### Domain model
![Database Diagram](Diagrams/Domain_Model.png)
### Use cases
![Use Cases](Diagrams/UseCases.png)
### UI design mockup
![UI demo](Diagrams/UIDesign.png)
 
