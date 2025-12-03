package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with enhanced functionality
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
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("pirateGreeting"));
        assertEquals("Ahoy matey! Welcome to our treasure chest of movies!", model.getAttribute("pirateGreeting"));
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        assertTrue(model.containsAttribute("movie"));
        assertTrue(model.containsAttribute("pirateGreeting"));
        assertEquals("Ahoy! Here be the details of yer chosen treasure:", model.getAttribute("pirateGreeting"));
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
        assertEquals("Movie Treasure Not Found", model.getAttribute("title"));
        assertTrue(((String) model.getAttribute("message")).contains("Arrr!"));
    }

    @Test
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("Prison", null, null, model);
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("pirateGreeting"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Space Wars", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Action", model);
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        String result = moviesController.searchMovies("Space", null, "Sci-Fi", model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Space Wars", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistent", null, null, model);
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("emptyResults"));
        assertTrue(model.containsAttribute("pirateGreeting"));
        
        String greeting = (String) model.getAttribute("pirateGreeting");
        assertTrue(greeting.contains("Arrr! No movie treasures found"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testSearchMoviesInvalidParameters() {
        String result = moviesController.searchMovies("", null, "", model);
        assertNotNull(result);
        assertEquals("error", result);
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
        assertTrue(model.containsAttribute("searchParams"));
        
        assertEquals("Invalid Search Parameters", model.getAttribute("title"));
        String message = (String) model.getAttribute("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("at least one search parameter"));
    }

    @Test
    public void testSearchMoviesNullParameters() {
        String result = moviesController.searchMovies(null, null, null, model);
        assertNotNull(result);
        assertEquals("error", result);
        assertTrue(model.containsAttribute("searchParams"));
    }

    @Test
    public void testSearchMoviesEmptyStringParameters() {
        String result = moviesController.searchMovies("   ", null, "   ", model);
        assertNotNull(result);
        assertEquals("error", result);
        assertTrue(model.containsAttribute("searchParams"));
    }

    @Test
    public void testSearchMoviesCaseInsensitive() {
        String result = moviesController.searchMovies("PRISON", null, null, model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesPartialMatch() {
        String result = moviesController.searchMovies("War", null, null, model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Space Wars", movies.get(0).getMovieName());
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
    }
}
