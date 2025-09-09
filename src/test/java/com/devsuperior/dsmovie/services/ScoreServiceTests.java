package com.devsuperior.dsmovie.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
    
    @InjectMocks
    private ScoreService service;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock 
    private UserService userService;

    @Mock
    private MovieRepository movieRepository;

    private UserEntity userLogged;
    private Long existingMovieId, nonExistingMovieId;
    private MovieEntity movieEntity;
    private MovieDTO movieDTO;
    private ScoreDTO scoreDTO;
    private ScoreEntity score;

    @BeforeEach
    void setUp() {
        existingMovieId = 1L;
        nonExistingMovieId = 2L;

        userLogged = UserFactory.createUserEntity();
        movieEntity = MovieFactory.createMovieEntity();
        movieDTO = new MovieDTO(movieEntity);

        score = new ScoreEntity();
        score.setMovie(movieEntity);
        score.setUser(userLogged);
        score.setValue(3.0);
        scoreDTO = new ScoreDTO(score);
        movieEntity.getScores().add(score);

        Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
        Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

        Mockito.when(movieRepository.save(movieEntity)).thenReturn(movieEntity);
        Mockito.when(scoreRepository.saveAndFlush(Mockito.any(ScoreEntity.class))).thenReturn(score);
    }
    
    @Test
    public void saveScoreShouldReturnMovieDTO() {
        Mockito.when(userService.authenticated()).thenReturn(userLogged);
        MovieDTO result = service.saveScore(scoreDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movieDTO.getId());
    }
    
    @Test
    public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        Mockito.when(userService.authenticated()).thenReturn(userLogged);

        ScoreDTO invalidScoreDTO = new ScoreDTO(nonExistingMovieId, 3.0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.saveScore(invalidScoreDTO);
        });
    }
}