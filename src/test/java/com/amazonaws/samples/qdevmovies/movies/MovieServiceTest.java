package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for MovieService with pirate flair!
 * Arrr! These tests ensure our treasure hunting methods work ship-shape!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertEquals(12, movies.size()); // Based on the movies.json file
        
        // Verify first movie
        Movie firstMovie = movies.get(0);
        assertEquals(1L, firstMovie.getId());
        assertEquals("The Prison Escape", firstMovie.getMovieName());
        assertEquals("John Director", firstMovie.getDirector());
        assertEquals(1994, firstMovie.getYear());
        assertEquals("Drama", firstMovie.getGenre());
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movieOpt = movieService.getMovieById(1L);
        
        assertTrue(movieOpt.isPresent());
        Movie movie = movieOpt.get();
        assertEquals(1L, movie.getId());
        assertEquals("The Prison Escape", movie.getMovieName());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movieOpt = movieService.getMovieById(999L);
        assertFalse(movieOpt.isPresent());
    }

    @Test
    public void testGetMovieByIdNull() {
        Optional<Movie> movieOpt = movieService.getMovieById(null);
        assertFalse(movieOpt.isPresent());
    }

    @Test
    public void testGetMovieByIdZero() {
        Optional<Movie> movieOpt = movieService.getMovieById(0L);
        assertFalse(movieOpt.isPresent());
    }

    @Test
    public void testGetMovieByIdNegative() {
        Optional<Movie> movieOpt = movieService.getMovieById(-1L);
        assertFalse(movieOpt.isPresent());
    }

    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("Family", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameMultipleResults() {
        List<Movie> results = movieService.searchMovies("The", null, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 1);
        // Should find multiple movies with "The" in the title
        assertTrue(results.stream().anyMatch(m -> m.getMovieName().contains("The")));
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 2L, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).getId());
        assertEquals("The Family Boss", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        // All results should have Drama in their genre
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Sci");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        // Should find Sci-Fi movies
        assertTrue(results.stream().anyMatch(m -> m.getGenre().toLowerCase().contains("sci")));
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "ACTION");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        // Should find Action movies
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("action")));
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        List<Movie> results = movieService.searchMovies("Hero", null, "Action");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        Movie movie = results.get(0);
        assertTrue(movie.getMovieName().toLowerCase().contains("hero"));
        assertTrue(movie.getGenre().toLowerCase().contains("action"));
    }

    @Test
    public void testSearchMoviesAllCriteria() {
        List<Movie> results = movieService.searchMovies("The Masked Hero", 3L, "Action");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        Movie movie = results.get(0);
        assertEquals(3L, movie.getId());
        assertEquals("The Masked Hero", movie.getMovieName());
        assertTrue(movie.getGenre().toLowerCase().contains("action"));
    }

    @Test
    public void testSearchMoviesNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesConflictingCriteria() {
        // Search for a specific ID but with a name that doesn't match
        List<Movie> results = movieService.searchMovies("NonExistent", 1L, null);
        
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Should be empty because name doesn't match
    }

    @Test
    public void testSearchMoviesEmptyName() {
        List<Movie> results = movieService.searchMovies("", null, null);
        
        assertNotNull(results);
        // Empty string should be treated as no search criteria for name
        // Since no other criteria provided, this should return all movies
        assertEquals(12, results.size());
    }

    @Test
    public void testSearchMoviesWhitespaceName() {
        List<Movie> results = movieService.searchMovies("   ", null, null);
        
        assertNotNull(results);
        // Whitespace-only string should be treated as no search criteria
        assertEquals(12, results.size());
    }

    @Test
    public void testSearchMoviesEmptyGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "");
        
        assertNotNull(results);
        // Empty string should be treated as no search criteria for genre
        assertEquals(12, results.size());
    }

    @Test
    public void testSearchMoviesWhitespaceGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "   ");
        
        assertNotNull(results);
        // Whitespace-only string should be treated as no search criteria
        assertEquals(12, results.size());
    }

    @Test
    public void testIsValidSearchRequestValidName() {
        assertTrue(movieService.isValidSearchRequest("Prison", null, null));
    }

    @Test
    public void testIsValidSearchRequestValidId() {
        assertTrue(movieService.isValidSearchRequest(null, 1L, null));
    }

    @Test
    public void testIsValidSearchRequestValidGenre() {
        assertTrue(movieService.isValidSearchRequest(null, null, "Drama"));
    }

    @Test
    public void testIsValidSearchRequestMultipleValid() {
        assertTrue(movieService.isValidSearchRequest("Prison", 1L, "Drama"));
    }

    @Test
    public void testIsValidSearchRequestAllNull() {
        assertFalse(movieService.isValidSearchRequest(null, null, null));
    }

    @Test
    public void testIsValidSearchRequestEmptyStrings() {
        assertFalse(movieService.isValidSearchRequest("", null, ""));
    }

    @Test
    public void testIsValidSearchRequestWhitespaceStrings() {
        assertFalse(movieService.isValidSearchRequest("   ", null, "   "));
    }

    @Test
    public void testIsValidSearchRequestZeroId() {
        assertFalse(movieService.isValidSearchRequest(null, 0L, null));
    }

    @Test
    public void testIsValidSearchRequestNegativeId() {
        assertFalse(movieService.isValidSearchRequest(null, -1L, null));
    }

    @Test
    public void testIsValidSearchRequestMixedValidInvalid() {
        // Valid name, invalid ID, empty genre
        assertTrue(movieService.isValidSearchRequest("Prison", 0L, ""));
        
        // Invalid name, valid ID, empty genre
        assertTrue(movieService.isValidSearchRequest("", 1L, ""));
        
        // Invalid name, invalid ID, valid genre
        assertTrue(movieService.isValidSearchRequest("", 0L, "Drama"));
    }

    @Test
    public void testSearchMoviesSpecificMoviesByGenre() {
        // Test searching for Crime/Drama movies
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        // Should find movies like "The Family Boss", "Urban Stories", "The Wise Guys"
        assertTrue(results.stream().anyMatch(m -> m.getMovieName().equals("The Family Boss")));
    }

    @Test
    public void testSearchMoviesSpecificMoviesByYear() {
        // Since we don't have year search in current implementation,
        // let's test searching by name for movies from specific years
        List<Movie> results = movieService.searchMovies("Space Wars", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Space Wars: The Beginning", results.get(0).getMovieName());
        assertEquals(1977, results.get(0).getYear());
    }

    @Test
    public void testSearchMoviesRatingConsistency() {
        // Test that search results maintain proper movie data
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        Movie movie = results.get(0);
        assertEquals(5.0, movie.getImdbRating());
        assertEquals(142, movie.getDuration());
        assertNotNull(movie.getDescription());
        assertTrue(movie.getDescription().length() > 0);
    }
}