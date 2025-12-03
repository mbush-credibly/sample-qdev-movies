package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Ahoy! Fetching all movie treasures for ye");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("pirateGreeting", "Ahoy matey! Welcome to our treasure chest of movies!");
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Arrr! Fetching details for movie treasure ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Shiver me timbers! Movie treasure with ID {} not found", movieId);
            model.addAttribute("title", "Movie Treasure Not Found");
            model.addAttribute("message", "Arrr! The movie treasure with ID " + movieId + " has sailed away and cannot be found, matey!");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        model.addAttribute("pirateGreeting", "Ahoy! Here be the details of yer chosen treasure:");
        
        return "movie-details";
    }

    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Arrr! Searching for movie treasures with name: {}, id: {}, genre: {}", name, id, genre);
        
        // Validate search parameters
        if (!movieService.isValidSearchRequest(name, id, genre)) {
            logger.warn("Shiver me timbers! Invalid search parameters provided");
            model.addAttribute("title", "Invalid Search Parameters");
            model.addAttribute("message", "Arrr! Ye need to provide at least one search parameter to hunt for treasures, matey!");
            model.addAttribute("searchParams", true);
            return "error";
        }
        
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        model.addAttribute("movies", searchResults);
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchName", name);
        model.addAttribute("searchId", id);
        model.addAttribute("searchGenre", genre);
        
        if (searchResults.isEmpty()) {
            model.addAttribute("pirateGreeting", "Arrr! No movie treasures found matching yer search criteria, matey!");
            model.addAttribute("emptyResults", true);
        } else {
            model.addAttribute("pirateGreeting", "Ahoy! Found " + searchResults.size() + " movie treasures matching yer search!");
        }
        
        return "movies";
    }
}