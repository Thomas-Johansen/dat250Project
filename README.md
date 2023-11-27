# Dat250Project

[![FeedApp Tests](../../actions/workflows/gradle.yml/badge.svg)](../../actions/workflows/gradle.yml)

This repo contains a Poll-system application, that lets users create polls and vote on polls made by other users.  

Link to front end of the application: https://github.com/MikalDr/Dat250project-frontend  

## Docker Image:
[![FeedApp Image build](../../actions/workflows/main.yml/badge.svg)](../../actions/workflows/main.yml)
The docker image for the backend of the project can be found following this link:
https://hub.docker.com/layers/mikaldr/dat250-project/latest/images/sha256-d272d2cd84f5c1bc056f3f0cfa49dbaccaf4415dc1fde1a50a4819182c6a4a1d?context=repo

### To run the application:
1. Pull the latest image using: `docker pull mikaldr/dat250-project:latest`
2. Run the image using `docker run -d -p 8080:8080 mikaldr/dat250-project:latest`
   This will open the port 8080 on your localmachine to the container.

Below is some of the diagrams created during the planning stage of the project:
### Application flow diagram
![Applicataion Flow Diagram](https://github.com/Thomas-Johansen/dat250Project/blob/main/Diagrams/Application%20Flow%20Diagram.png?raw=true)
### AoT
![AoT](Diagrams/ArchitecturalDiagram.png)
### Database Diagram
![Database Diagram](https://github.com/Thomas-Johansen/dat250Project/blob/main/Diagrams/Database.drawio%20(4).png?raw=true)
### Use cases
![Use Cases](https://github.com/Thomas-Johansen/dat250Project/blob/main/Diagrams/Use%20cases.png?raw=true)
### UI demo
![UI demo](https://github.com/Thomas-Johansen/dat250Project/blob/main/Diagrams/UserInterfaceDemo.png?raw=true)
 
