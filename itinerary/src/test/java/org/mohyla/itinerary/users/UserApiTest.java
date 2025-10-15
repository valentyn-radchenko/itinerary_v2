package org.mohyla.itinerary.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mohyla.itinerary.users.application.UsersService;
import org.mohyla.itinerary.users.domain.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserApi.class)
class UserApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser_Success() throws Exception {
        User userRequest = new User("John Doe", "john@example.com");
        User expectedUser = new User("John Doe", "john@example.com");
        
        when(usersService.createUser(anyString(), anyString())).thenReturn(expectedUser);
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
        
        verify(usersService).createUser("John Doe", "john@example.com");
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        List<User> expectedUsers = Arrays.asList(
            new User("John", "john@example.com"),
            new User("Jane", "jane@example.com")
        );
        when(usersService.getAllUsers()).thenReturn(expectedUsers);
        
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[1].name").value("Jane"));
        
        verify(usersService).getAllUsers();
    }
}
