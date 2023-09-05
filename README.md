# CSC-7057-DepScanner-Project
DepScanner Project

## Overview
An application that allows users to upload a dependency-management file from a project which then parses the open source dependencies
and retrieves security metadata from the DEPS.DEV API (https://deps.dev/). The application handles user authentication with keycloak, complete with user sign up, login or SSO with GitHub.
The application also contains scanning functionality which will scan the users project for updated security advisories either weekly, daily or not at all (as defined by the user).
A notification service also provides email notifications to the user if vulnerabilities are detected.
The frontend user-interface has been developed with React, Material UI and communication with the keycloak realm with OIDC.
The backend has been developed with Spring Boot (Java) with a microservices based architecture.

## Microservice breakdown:
### Upload Service:
- A user uploads a valid dependency management file from the root of their project file, and it is received by the notification service
- The notification service determines if the file is correct (file path, file structure etc) and can determine the ecosystem from the file upload.
- The service will scan the file for dependency information – the key information being the dependency name and version.

### Project Service:
-	Responsible for creating, reading, updating and scanning user projects. Data storage handled within MongoDB. Authentication is implemented through the use of an Open Source Identity and Access Management service (Keycloak), this ensures a user can only access their own relevant project data. Keycloak can also authenticate SSO with existing OpenID Connect providers such as GitHub. Once a new project is created, an Apache Kafka event is produced and received by the Vuln service. The project service can also communicate synchronously with the vulnerability service to determine which dependencies within a project are vulnerable. 

### Vuln Service:
-	Responsible for obtaining and managing open-source dependency vulnerability data. This data is obtained directly from the deps.dev API and then stored within the service PostgreSQL database. Because of the nature of vulnerabilities always being discovered, it is important that dependencies are rescanned for up to date security information and updating stored data within the service database. When vulnerable dependencies are discovered, the associated project data and vulnerable data is sent to the notification service via Kafka.

### Notification Service:
-	Responsible for receiving vulnerable data (user project details, vulnerable dependency information) from the vulnerability service via Apache Kafka events. The notification service then sends an email to the registered user email address in the project via JavaMailSender. JavaMailSender is configured with a Simple Mail Transfer Protocol (SMTP) which can send emails with HTML templates using a service such as Thyme leaf.

### UML Overview:
![image](https://github.com/agrainger14/csc-7057-depscanner-project/assets/132609173/ed40308a-6697-44c0-bf45-02ac589dac80)

## How to run
The microservices are available as docker images and the API can be ran as a container.

_Docker Compose_

```bash
cd microservices-parent
docker-compose up --build -d
```
the API is accessed on http://localhost:8080, the keycloak realm is included and will be automatically imported on container build.
To use the notification service and configure keycloak password reset, a valid email user/password config is required. This project used gmail as an example notification service.

_DepScanner UI_

```bash
cd depscanner-ui
npm i
npm run dev
```
The UI is accessed on http://localhost:5173

