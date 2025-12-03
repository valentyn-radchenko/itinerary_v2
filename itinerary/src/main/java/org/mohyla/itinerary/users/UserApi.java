package org.mohyla.itinerary.users;

import org.mohyla.itinerary.grpc.PaymentClient;
import org.mohyla.itinerary.users.application.UsersService;
import org.mohyla.itinerary.users.domain.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserApi {
    private final UsersService usersService;
    private final PaymentClient paymentClient;
    public UserApi(UsersService usersService, PaymentClient paymentClient) {
        this.usersService = usersService;
        this.paymentClient = paymentClient;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User request){
        System.out.println("Creating user");
        System.out.println(request.getName());
        User created = usersService.createUser(request.getName(), request.getEmail());
        return ResponseEntity.ok(created);
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return usersService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}/list-payments")
    public ResponseEntity<String> listUserPayments(@PathVariable Long id){
        paymentClient.streamPaymentsForUser(id);
        return ResponseEntity.ok("Payments Listed");
    }
}
