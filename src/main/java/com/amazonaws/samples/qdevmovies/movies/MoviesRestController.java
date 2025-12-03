package com.amazonaws.samples.qdevmovies.movies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API Controller for movie operations with pirate flair!
 * Ahoy matey! This controller handles all the REST API treasure hunting operations.
 */
@RestController
@RequestMapping("/api")
public class MoviesRestController {
    private static final Logger logger = LogManager.getLogger(MoviesRestController.class);

    @Autowired
    private MovieService movieService;

    /**
     * Get all movie treasures via REST API
     * Arrr! Returns all movies in our treasure chest as JSON
     */
    @GetMapping("/movies")
    public ResponseEntity<Map<String, Object>> getAllMovies() {
        logger.info("Ahoy! REST API request for all movie treasures");
        
        Map<String, Object> response = new HashMap<>();
        List<Movie> movies = movieService.getAllMovies();
        
        response.put("message", "Ahoy matey! Here be all the movie treasures in our collection!");
        response.put("movies", movies);
        response.put("count", movies.size());
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific movie treasure by ID via REST API
     * Arrr! Returns a single movie treasure if found
     */
    @GetMapping("/movies/{id}")
    public ResponseEntity<Map<String, Object>> getMovieById(@PathVariable("id") Long movieId) {
        logger.info("Arrr! REST API request for movie treasure ID: {}", movieId);
        
        Map<String, Object> response = new HashMap<>();
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        
        if (!movieOpt.isPresent()) {
            logger.warn("Shiver me timbers! Movie treasure with ID {} not found via REST API", movieId);
            response.put("message", "Arrr! The movie treasure with ID " + movieId + " has sailed away and cannot be found, matey!");
            response.put("status", "error");
            response.put("errorCode", "MOVIE_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Movie movie = movieOpt.get();
        response.put("message", "Ahoy! Found yer movie treasure!");
        response.put("movie", movie);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search for movie treasures via REST API
     * Ahoy! This be the main treasure hunting endpoint for REST API calls
     */
    @GetMapping("/movies/search")
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Arrr! REST API search for movie treasures with name: {}, id: {}, genre: {}", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate search parameters
        if (!movieService.isValidSearchRequest(name, id, genre)) {
            logger.warn("Shiver me timbers! Invalid search parameters provided via REST API");
            response.put("message", "Arrr! Ye need to provide at least one search parameter to hunt for treasures, matey!");
            response.put("status", "error");
            response.put("errorCode", "INVALID_SEARCH_PARAMETERS");
            response.put("validParameters", List.of("name", "id", "genre"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        // Build search criteria for response
        Map<String, Object> searchCriteria = new HashMap<>();
        if (name != null && !name.trim().isEmpty()) {
            searchCriteria.put("name", name);
        }
        if (id != null) {
            searchCriteria.put("id", id);
        }
        if (genre != null && !genre.trim().isEmpty()) {
            searchCriteria.put("genre", genre);
        }
        
        response.put("searchCriteria", searchCriteria);
        response.put("movies", searchResults);
        response.put("count", searchResults.size());
        response.put("status", "success");
        
        if (searchResults.isEmpty()) {
            response.put("message", "Arrr! No movie treasures found matching yer search criteria, matey! Try different search terms.");
        } else {
            response.put("message", "Ahoy! Found " + searchResults.size() + " movie treasures matching yer search!");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Handle invalid movie ID format
     * Arrr! This handles when landlubbers provide invalid treasure IDs
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMovieId(NumberFormatException e) {
        logger.warn("Shiver me timbers! Invalid movie ID format provided: {}", e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Arrr! That be not a valid treasure ID, matey! Use numbers only.");
        response.put("status", "error");
        response.put("errorCode", "INVALID_MOVIE_ID_FORMAT");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle general exceptions
     * Arrr! This handles when the ship encounters rough seas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        logger.error("Arrr! Unexpected error occurred: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Shiver me timbers! Something went wrong on our ship. Please try again later, matey!");
        response.put("status", "error");
        response.put("errorCode", "INTERNAL_SERVER_ERROR");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}