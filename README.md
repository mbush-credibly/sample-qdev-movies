# Movie Service - Spring Boot Demo Application 🏴‍☠️

Ahoy matey! A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate flair!

## Features

- **Movie Treasure Chest**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including captain (director), year of discovery, type of adventure (genre), length of journey (duration), and treasure map (description)
- **Treasure Hunt (Search & Filter)**: Search for movie treasures by name, ID, or genre with our powerful search functionality
- **Crew Reviews**: Each movie includes authentic crew member reviews with ratings and avatars
- **REST API**: Full REST API support for treasure hunting operations
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate Language**: Arrr! All interactions use authentic pirate language for a fun experience

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Thymeleaf** for server-side templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie Treasure Chest**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search for Treasures**: Use the search form on the main page or visit http://localhost:8080/movies/search

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── MoviesApplication.java         # Main Spring Boot application
│   │       ├── MoviesController.java          # HTML controller for movie endpoints
│   │       ├── MoviesRestController.java      # REST API controller for JSON responses
│   │       ├── Movie.java                     # Movie data model
│   │       ├── MovieService.java              # Business logic with search functionality
│   │       ├── Review.java                    # Review data model
│   │       ├── ReviewService.java             # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java        # Movie icon utilities
│   │           └── MovieUtils.java            # Movie validation utilities
│   └── resources/
│       ├── templates/
│       │   ├── movies.html                    # Main movie listing with search form
│       │   ├── movie-details.html             # Movie details page
│       │   └── error.html                     # Error page with pirate messages
│       ├── application.yml                    # Application configuration
│       ├── movies.json                        # Movie treasure data
│       ├── mock-reviews.json                  # Mock review data
│       └── log4j2.xml                         # Logging configuration
└── test/                                      # Comprehensive unit tests
```

## API Endpoints

### HTML Endpoints (Web Interface)

#### Get All Movie Treasures
```
GET /movies
```
Returns an HTML page displaying all movies with search functionality and pirate greetings.

#### Get Movie Treasure Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and crew reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

#### Search for Movie Treasures
```
GET /movies/search
```
Returns an HTML page with filtered movie results based on search criteria.

**Query Parameters:**
- `name` (optional): Movie name (partial matches supported, case-insensitive)
- `id` (optional): Specific movie ID (1-12)
- `genre` (optional): Genre filter (partial matches supported, case-insensitive)

**Examples:**
```
http://localhost:8080/movies/search?name=Prison
http://localhost:8080/movies/search?genre=Action
http://localhost:8080/movies/search?id=5
http://localhost:8080/movies/search?name=Space&genre=Sci-Fi
```

### REST API Endpoints (JSON Responses)

#### Get All Movie Treasures (REST)
```
GET /api/movies
```
Returns JSON response with all movies and pirate messages.

**Response Format:**
```json
{
  "message": "Ahoy matey! Here be all the movie treasures in our collection!",
  "movies": [...],
  "count": 12,
  "status": "success"
}
```

#### Get Movie Treasure by ID (REST)
```
GET /api/movies/{id}
```
Returns JSON response with specific movie details.

**Response Format:**
```json
{
  "message": "Ahoy! Found yer movie treasure!",
  "movie": {...},
  "status": "success"
}
```

#### Search Movie Treasures (REST)
```
GET /api/movies/search
```
Returns JSON response with filtered movie results.

**Query Parameters:**
- `name` (optional): Movie name filter
- `id` (optional): Movie ID filter
- `genre` (optional): Genre filter

**Response Format:**
```json
{
  "message": "Ahoy! Found 3 movie treasures matching yer search!",
  "movies": [...],
  "count": 3,
  "searchCriteria": {
    "name": "Action",
    "genre": "Adventure"
  },
  "status": "success"
}
```

**Error Response Format:**
```json
{
  "message": "Arrr! Ye need to provide at least one search parameter to hunt for treasures, matey!",
  "status": "error",
  "errorCode": "INVALID_SEARCH_PARAMETERS",
  "validParameters": ["name", "id", "genre"]
}
```

## Search Functionality

### Supported Search Operations

1. **Name Search**: Partial, case-insensitive matching
   - Example: "Prison" matches "The Prison Escape"
   - Example: "war" matches "Space Wars: The Beginning"

2. **ID Search**: Exact match by movie ID
   - Example: `id=1` returns "The Prison Escape"

3. **Genre Search**: Partial, case-insensitive matching
   - Example: "Action" matches "Action/Crime" and "Action/Sci-Fi"
   - Example: "sci" matches "Sci-Fi" genres

4. **Combined Search**: Multiple criteria (AND operation)
   - Example: `name=Hero&genre=Action` finds action movies with "Hero" in the title

### Search Validation

- At least one search parameter must be provided
- Empty strings and whitespace-only strings are treated as invalid
- Movie ID must be a positive number
- Invalid searches return helpful error messages with pirate language

## Error Handling

The application provides comprehensive error handling with pirate-themed messages:

- **Movie Not Found**: "Arrr! The movie treasure with ID X has sailed away and cannot be found, matey!"
- **Invalid Search Parameters**: "Arrr! Ye need to provide at least one search parameter to hunt for treasures, matey!"
- **Server Errors**: "Shiver me timbers! Something went wrong on our ship. Please try again later, matey!"

## Testing

Run the comprehensive test suite:

```bash
mvn test
```

The test suite includes:
- **MoviesControllerTest**: Tests for HTML controller with search functionality
- **MoviesRestControllerTest**: Tests for REST API endpoints
- **MovieServiceTest**: Tests for business logic and search operations
- **MovieTest**: Tests for movie model

### Test Coverage

- Search functionality (name, ID, genre, combined searches)
- Edge cases (empty results, invalid parameters)
- Error handling and validation
- Pirate language integration
- Case-insensitive and partial matching
- REST API responses and error codes

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

Ensure you provide at least one valid search parameter:
- Name: Non-empty string
- ID: Positive number (1-12)
- Genre: Non-empty string

## Contributing

This project demonstrates modern Spring Boot development with fun pirate theming. Feel free to:
- Add more movies to the treasure chest
- Enhance the search functionality (add year, director, rating filters)
- Improve the pirate language and UI/UX
- Add more comprehensive error handling
- Implement additional REST API features

## Pirate Language Guide

The application uses authentic pirate language throughout:
- **Ahoy!** - Greeting
- **Matey** - Friend/user
- **Arrr!** - Emphasis/frustration
- **Treasure** - Movies
- **Captain** - Director
- **Crew** - Users/reviewers
- **Ship** - Application/server
- **Treasure Chest** - Movie collection
- **Hunt/Search** - Search functionality

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye explore our movie treasures! 🏴‍☠️*
