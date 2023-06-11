package ru.senya.pixatekaserv.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.pixatekaserv.models.User;
import ru.senya.pixatekaserv.repo.UserRepository;

import java.util.NoSuchElementException;

@RestController
public class UsersController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public Iterable<User> users() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> user(@PathVariable Long id) {
        try {
            return ResponseEntity
                    .ok()
                    .body(userRepository.findById(id).orElseThrow());
        } catch (NoSuchElementException exception) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam("username") String username,
                                      @RequestParam("passsword") int password) {
        return null;
    }
}
