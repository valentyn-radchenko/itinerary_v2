package org.mohyla.itinerary.users.application;

import org.mohyla.itinerary.users.UserApi;
import org.mohyla.itinerary.users.domain.events.UserCreatedEvent;
import org.mohyla.itinerary.users.domain.models.User;
import org.mohyla.itinerary.users.domain.persistence.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;


    public UsersService(UserRepository userRepository, ApplicationEventPublisher events) {
        this.userRepository = userRepository;
        this.events = events;
    }

    public User createUser(String name, String email){
        User user = new User(name, email);
        System.out.println("In service");
        System.out.println("Name " + name);
        System.out.println(user.getName());
        userRepository.save(user);

        events.publishEvent(new UserCreatedEvent(user.getId(), user.getName(), user.getEmail()));
        return user;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }
}
