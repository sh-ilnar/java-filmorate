package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc.perform(delete("/users"));
    }

    @Test
    public void givenValidUser_whenCreate_thenUserIsCreated() throws Exception {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                null
        );

        userService.addUser(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.login").value("testLogin"))
                .andExpect(jsonPath("$.name").value("adipisicing"))
                .andExpect(jsonPath("$.birthday").value("1967-03-25"));
    }

    @Test
    public void givenInvalidEmail_whenCreate_thenReturnError() throws Exception {
        User user = new User(
                1,
                "invalidEmail",
                "testLogin",
                "adipisicing",
                LocalDate.of(1967, 3, 25),
                null
        );

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenEmptyUserObject_whenCreate_thenReturnError() throws Exception {
        String jsonBody = "{ \"id\": \"1\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenCreatedUsers_whenGetUsers_thenReturnUsers() throws Exception {
        User user1 = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        User user2 = new User(
                2,
                "test2@test.com",
                "testLogin2",
                "name2",
                LocalDate.of(1999, 1, 10),
                null
        );

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void givenCreatedUser_whenUpdate_thenUserIsUpdated() throws Exception {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        user.setEmail("anotherTest@test.com");
        user.setLogin("anotherLogin");
        user.setName("anotherName");
        user.setBirthday(LocalDate.of(2000, 4, 20));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("anotherTest@test.com"))
                .andExpect(jsonPath("$.login").value("anotherLogin"))
                .andExpect(jsonPath("$.name").value("anotherName"))
                .andExpect(jsonPath("$.birthday").value("2000-04-20"));
    }

    @Test
    public void givenCreatedUser_whenDeleteAll_thenUsersAreDeleted() throws Exception {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );

        userService.addUser(user);

        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void givenCreatedUserFriend_whenPutFriend_thenFriendIsAdded() throws Exception {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        User friend = new User(
                2,
                "test2@test.com",
                "testLogin2",
                "name2",
                LocalDate.of(1999, 1, 10),
                null
        );

        userService.addUser(user);
        userService.addUser(friend);

        mockMvc.perform(put("/users/{userId}/friends/{friendId}", user.getId(), friend.getId()))
                .andExpect(status().isOk());

        assertTrue(user.getFriends().contains(friend.getId()));
    }
}
