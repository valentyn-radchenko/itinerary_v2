package org.mohyla.itinerary.users.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mohyla.itinerary.users.domain.events.UserCreatedEvent;
import org.mohyla.itinerary.users.domain.models.User;
import org.mohyla.itinerary.users.domain.persistence.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher events;

    @InjectMocks
    private UsersService usersService;

    @Test
    void testCreateUser_Success() {
        String name = "John Doe";
        String email = "john@example.com";
        User expectedUser = new User(name, email);
        
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        
        User result = usersService.createUser(name, email);
        
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).save(any(User.class));
        verify(events).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void testGetAllUsers_Success() {
        List<User> expectedUsers = Arrays.asList(
            new User("John", "john@example.com"),
            new User("Jane", "jane@example.com")
        );
        when(userRepository.findAll()).thenReturn(expectedUsers);
        
        List<User> result = usersService.getAllUsers();
        
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedUsers);
        verify(userRepository).findAll();
    }
}
