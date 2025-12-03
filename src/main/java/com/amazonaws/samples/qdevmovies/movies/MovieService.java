package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Searches for movies based on the provided criteria with pirate flair.
     * Ahoy matey! This method helps ye hunt for treasure in our movie collection!
     * 
     * @param name Movie name to search for (partial matches allowed)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Searching for movie treasures with name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        for (Movie movie : movies) {
            boolean matches = true;
            
            // Search by ID if provided
            if (id != null && movie.getId() != id) {
                matches = false;
            }
            
            // Search by name if provided (case-insensitive partial match)
            if (name != null && !name.trim().isEmpty()) {
                String searchName = name.trim().toLowerCase();
                String movieName = movie.getMovieName().toLowerCase();
                if (!movieName.contains(searchName)) {
                    matches = false;
                }
            }
            
            // Search by genre if provided (case-insensitive partial match)
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
        
        logger.info("Ahoy! Found {} movie treasures matching yer search criteria", results.size());
        return results;
    }

    /**
     * Validates search parameters to ensure they're ship-shape!
     * 
     * @param name Movie name parameter
     * @param id Movie ID parameter
     * @param genre Genre parameter
     * @return true if at least one valid search parameter is provided
     */
    public boolean isValidSearchRequest(String name, Long id, String genre) {
        // At least one parameter should be provided and valid
        boolean hasValidName = name != null && !name.trim().isEmpty();
        boolean hasValidId = id != null && id > 0;
        boolean hasValidGenre = genre != null && !genre.trim().isEmpty();
        
        return hasValidName || hasValidId || hasValidGenre;
    }
}
