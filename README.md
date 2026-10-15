REFLEX TIMER GAME
=================
APP pictures located images folder
----------------------------------

Technologies:
- Java 21
- JavaFX desktop UI
- Spring Boot REST API
- Spring Data JPA / Hibernate
- MySQL database

FEATURES
1. Player enters a name and can view their last two saved results.
2. Game uses two button presses. First press starts a random target (2-8 seconds); second press records elapsed time.
3. A round is a win when elapsed time is within 50 milliseconds of the target.
4. TOP 10 ranks scores by smallest absolute time difference.
5. Each score stores ID, player name, target timer milliseconds, exact measured elapsed milliseconds,
   difference, win status and timestamp.

REQUIREMENTS
- JDK 21+
- Maven
- MySQL Server

DATABASE SETUP
The app uses a database called reflex_game. application.properties defaults:
  jdbc:mysql://localhost:3306/reflex_game?createDatabaseIfNotExist=true
  username=root
  password=empty

Set DB_USERNAME and DB_PASSWORD environment variables if needed, or edit
src/main/resources/application.properties. Ensure MySQL is running.

RUN
Open the project folder in IntelliJ as a Maven project and wait for dependencies to download.
Run the desktop application:
  mvn javafx:run

The JavaFX app starts the Spring Boot API automatically on port 8080.
Alternatively, to run the API separately:
  mvn spring-boot:run
Then launch ReflexGameApp from IntelliJ (avoid starting the API twice).

REST ENDPOINTS
GET  http://localhost:8080/api/players/{name}/recent
POST http://localhost:8080/api/scores
GET  http://localhost:8080/api/scores/top10

Example POST JSON:
{
  "playerName": "Misho",
  "targetMs": 3000,
  "elapsedMs": 2985,
  "differenceMs": 15,
  "won": true
}

NOTES
- Timing is measured locally with System.nanoTime() and saved in milliseconds. It is not
  hardware-level timing; OS scheduling and JavaFX event handling affect precision.
- The app uses MySQL by default. For Firebase/online hosting, database and API configuration
  must be changed and credentials must never be committed to Git.
- The current leaderboard is formatted as JSON text in the UI; it can be improved into
  dedicated styled rows as a next step.
- I worked on the project together with AI.

