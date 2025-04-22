package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc.perform(delete("/films"));
    }

    @Test
    public void givenValidFilm_whenCreate_thenFilmIsCreated() throws Exception {

        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                100,
                null
        );

        filmService.addFilm(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("nisi eiusmod"))
                .andExpect(jsonPath("$.description").value("adipisicing"))
                .andExpect(jsonPath("$.releaseDate").value("1967-03-25"))
                .andExpect(jsonPath("$.duration").value(100));
    }

    @Test
    public void givenInvalidDescription_whenCreate_thenReturnError() throws Exception {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicin asdfas g asfdasfasdf asdfasdfas asdfasdfasdfasdfasfs asfasdfasdfasdfasdfadsfasdfa asfasdfasdfasdfasdfadsfasdfa asfasdfasdfasdfasdfadsfasdfa asfasdfasdfasdfasdfadsfasdfa asfasdfasdfasdfasdfadsfasdfa",
                LocalDate.of(1967, 3, 25),
                100,
                null
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenEmptyFilmObject_whenCreate_thenReturnError() throws Exception {
        String jsonBody = "{ \"id\": \"1\" }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenCreatedFilms_whenGetFilms_thenReturnFilms() throws Exception {
        Film film1 = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                100,
                null
        );
        Film film2 = new Film(
                2,
                "eiusmod",
                "qwerqwre",
                LocalDate.of(1999, 1, 10),
                120,
                null
        );

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film1)));

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film2)));

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void givenCreatedFilm_whenUpdate_thenFilmIsUpdated() throws Exception {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                100,
                null
        );

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        film.setName("asdfasdf");
        film.setDescription("zxcvzxcv");
        film.setReleaseDate(LocalDate.of(1967, 4, 20));
        film.setDuration(120);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("asdfasdf"))
                .andExpect(jsonPath("$.description").value("zxcvzxcv"))
                .andExpect(jsonPath("$.releaseDate").value("1967-04-20"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    public void givenCreatedFilm_whenDeleteAll_thenFilmsAreDeleted() throws Exception {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                100,
                null
        );

        filmService.addFilm(film);

        mockMvc.perform(delete("/films"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void givenCreatedFilmUser_whenPutLike_thenFilmIsLiked() throws Exception {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                100,
                null
        );

        filmService.addFilm(film);

        User user = new User(
                1,
                "invalidEmail",
                "testLogin",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                null
        );

        userService.addUser(user);

        mockMvc.perform(put("/films/{filmId}/like/{userId}", film.getId(), user.getId()))
                .andExpect(status().isOk());

        assertTrue(film.getLikes().contains(user.getId()));
    }
}
