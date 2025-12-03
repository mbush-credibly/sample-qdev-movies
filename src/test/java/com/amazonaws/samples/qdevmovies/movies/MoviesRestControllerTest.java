package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesRestControllerTest {

    private MoviesRestController restController;
    private MovieService mockMovieService;

    @BeforeEach
    public void setUp() {
        restController = new MoviesRestController();
        
        // Create mock service with enhanced functionality
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "The Prison Escape", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Space Wars", "Another Director", 2022, "Sci-Fi", "Space adventure", 130, 4.0),
                new Movie(3L, "Action Hero", "Action Director", 2021, "Action", "Hero story", 110, 3.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(movie -> movie.getId() == id).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> results = new ArrayList<>();
                for (Movie movie : testMovies) {
                    boolean matches = true;
                    
                    if (id != null && movie.getId() != id) {
                        matches = false;
                    }
                    
                    if (name != null && !name.trim().isEmpty()) {
                        String searchName = name.trim().toLowerCase();
                        String movieName = movie.getMovieName().toLowerCase();
                        if (!movieName.contains(searchName)) {
                            matches = false;
                        }
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        String searchGenre = genre.trim().toLowerCase();
                        String movieGenre = movie.getGenre().toLowerCase();
                        if (!movieGenre.contains(searchGenre)) {
                            matches = false;
                        }
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                return results;
            }
            
            @Override
            public boolean isValidSearchRequest(String name, Long id, String genre) {
                boolean hasValidName = name != null && !name.trim().isEmpty();
                boolean hasValidId = id != null && id > 0;
                boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
                
                return hasValidName || hasValidId || hasValidGenre;
            }
        };
        
        // Inject mock using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesRestController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(restController, mockMovieService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock service", e);
        }
    }

    @Test
    public void testGetAllMovies() {
        ResponseEntity<Map<String, Object>> response = restController.getAllMovies();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertTrue(((String) body.get("message")).contains("Ahoy matey!"));
        assertEquals(3, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(3, movies.size());
    }

    @Test
    public void testGetMovieById() {
        ResponseEntity<Map<String, Object>> response = restController.getMovieById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertTrue(((String) body.get("message")).contains("Ahoy!"));
        
        Movie movie = (Movie) body.get("movie");
        assertNotNull(movie);
        assertEquals("The Prison Escape", movie.getMovieName());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        ResponseEntity<Map<String, Object>> response = restController.getMovieById(999L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("MOVIE_NOT_FOUND", body.get("errorCode"));
        assertTrue(((String) body.get("message")).contains("Arrr!"));
    }

    @Test
    public void testSearchMoviesByName() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies("Prison", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        assertTrue(((String) body.get("message")).contains("Ahoy!"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("Prison", searchCriteria.get("name"));
    }

    @Test
    public void testSearchMoviesById() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies(null, 2L, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Space Wars", movies.get(0).getMovieName());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals(2L, searchCriteria.get("id"));
    }

    @Test
    public void testSearchMoviesByGenre() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies(null, null, "Action");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("Action", searchCriteria.get("genre"));
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies("Space", null, "Sci-Fi");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Space Wars", movies.get(0).getMovieName());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("Space", searchCriteria.get("name"));
        assertEquals("Sci-Fi", searchCriteria.get("genre"));
    }

    @Test
    public void testSearchMoviesNoResults() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies("NonExistent", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(0, body.get("count"));
        assertTrue(((String) body.get("message")).contains("Arrr! No movie treasures found"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testSearchMoviesInvalidParameters() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies("", null, "");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("INVALID_SEARCH_PARAMETERS", body.get("errorCode"));
        assertTrue(((String) body.get("message")).contains("Arrr!"));
        assertTrue(((String) body.get("message")).contains("at least one search parameter"));
        
        @SuppressWarnings("unchecked")
        List<String> validParameters = (List<String>) body.get("validParameters");
        assertTrue(validParameters.contains("name"));
        assertTrue(validParameters.contains("id"));
        assertTrue(validParameters.contains("genre"));
    }

    @Test
    public void testSearchMoviesNullParameters() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies(null, null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("INVALID_SEARCH_PARAMETERS", body.get("errorCode"));
    }

    @Test
    public void testSearchMoviesCaseInsensitive() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies("PRISON", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesPartialMatch() {
        ResponseEntity<Map<String, Object>> response = restController.searchMovies("War", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Space Wars", movies.get(0).getMovieName());
    }

    @Test
    public void testHandleInvalidMovieId() {
        ResponseEntity<Map<String, Object>> response = restController.handleInvalidMovieId(
            new NumberFormatException("Invalid number format"));
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("INVALID_MOVIE_ID_FORMAT", body.get("errorCode"));
        assertTrue(((String) body.get("message")).contains("Arrr!"));
        assertTrue(((String) body.get("message")).contains("not a valid treasure ID"));
    }

    @Test
    public void testHandleGeneralException() {
        ResponseEntity<Map<String, Object>> response = restController.handleGeneralException(
            new RuntimeException("Test exception"));
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("INTERNAL_SERVER_ERROR", body.get("errorCode"));
        assertTrue(((String) body.get("message")).contains("Shiver me timbers!"));
        assertTrue(((String) body.get("message")).contains("something went wrong"));
    }
}