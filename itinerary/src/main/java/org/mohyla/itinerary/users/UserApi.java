package org.mohyla.itinerary.users;

import lombok.extern.slf4j.Slf4j;
import org.mohyla.itinerary.grpc.PaymentClient;
import org.mohyla.itinerary.security.SecurityUtils;
import org.mohyla.itinerary.users.application.UsersService;
import org.mohyla.itinerary.users.domain.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserApi {
    private final UsersService usersService;
    private final PaymentClient paymentClient;
    private final SecurityUtils securityUtils;
    
    public UserApi(UsersService usersService, PaymentClient paymentClient, SecurityUtils securityUtils) {
        this.usersService = usersService;
        this.paymentClient = paymentClient;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User request){
        log.info("Received request to create user: {}", request.getName());
        User created = usersService.createUser(request.getName(), request.getEmail());
        return ResponseEntity.ok(created);
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Users can only access their own profile
        if (!currentUserId.equals(id)) {
            log.warn("User {} attempted to access profile of user {}", currentUserId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only access your own profile");
        }
        
        return usersService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/list-payments")
    public ResponseEntity<?> listUserPayments(@PathVariable Long id){
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Users can only view their own payments
        if (!currentUserId.equals(id)) {
            log.warn("User {} attempted to access payments of user {}", currentUserId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only view your own payments");
        }
        
        paymentClient.streamPaymentsForUser(id);
        return ResponseEntity.ok("Payments Listed");
    }
    
    // Endpoint for getting current user's profile
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Long currentUserId = securityUtils.getCurrentUserId();
        log.info("Getting current user profile: userId={}", currentUserId);
        
        return usersService.getUser(currentUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
