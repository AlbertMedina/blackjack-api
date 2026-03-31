# Blackjack API

## Description
This project implements a reactive API to manage a Blackjack game using Spring Boot and Spring WebFlux.
The API handles players and game sessions, while enforcing the rules of Blackjack. 
Game data is persisted in MongoDB (Reactive), and player information and rankings are stored in MySQL (JPA).  
It exposes endpoints for creating and deleting games, playing rounds, managing players, and retrieving player rankings.

## Architecture & Tech Stack
Built with Java 21 and Spring Boot 3.5.8, this project follows a Layered Architecture (Controller-Service-Repository) to ensure a clean separation of concerns and maintainability across the game logic and player management.
- **Controller Layer**  
  Exposes the reactive REST API endpoints using Spring WebFlux, handling incoming requests and mapping them to the appropriate service operations.
- **Service Layer**  
  Contains the core Blackjack business logic, rules, and game state management, coordinating interactions between players, hands, and the dealer.
- **Repository Layer**  
  Manages data access through Spring Data, providing an abstraction for both reactive and relational persistence.
- **Reactive Programming**  
  Utilizes Spring WebFlux to provide a non-blocking, asynchronous API, ensuring high performance and scalability for managing multiple concurrent game sessions.
- **Persistence**  
  Dual Persistence Hybrid storage approach: MongoDB (Reactive) for fast, document-based game session data, and MySQL for structured player information and rankings.
- **Testing**  
  Automated testing using JUnit and Mockito to validate game logic verify that the API responds as expected.
- **Containerization**  
  Uses Docker to package the application and its databases, making it easy to run the entire project in any environment without manual setup.
- **Documentation**  
  Interactive API documentation generated with OpenAPI (Swagger UI), allowing easy exploration of game endpoints and player management.
  
## Endpoints

### Games
| Method | Endpoint          | Description                 | Request Body               |
|--------|-------------------|-----------------------------|----------------------------|
| POST   | /game/new         | Create a new Blackjack game | `{ "playerName": "John" }` |
| GET    | /game/{id}        | Get game information        | N/A                        |
| POST   | /game/{id}/play   | Play an existing game       | `{ "action": "HIT" }`      |
| DELETE | /game/{id}/delete | Delete an existing game     | N/A                        |

### Players
| Method | Endpoint     | Description                                  | Request Body         |
|--------|--------------|----------------------------------------------|----------------------|
| GET    | /ranking     | Get the ranking of players by number of wins | N/A                  |
| PUT    | /player/{id} | Update player name                           | `{ "name": "John" }` |

## Installation
1. Clone repository (https://github.com/AlbertMedina/blackjack-api.git).
```
git clone https://github.com/AlbertMedina/blackjack-api.git
```
2. Navigate to project folder.
```
cd blackjack-api
```

## Execution
1. Ensure Docker is installed and running.
2. Pull the image from Docker Hub:
```
docker pull albertmc/blackjack-api:latest
```
or build it locally:
```
docker build -t blackjack-api .
```
3. Start the application and databases with Docker Compose:
```
docker-compose up
```
This will start MongoDB, MySQL and the Spring Boot API.
The API will be available at:
```
http://localhost:8080
```
The API documentation will be available via Swagger at:
```
http://localhost:8080/swagger-ui.html
```
