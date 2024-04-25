package com.c174.controllers;


import com.c174.exception.EntityExistsException;
import com.c174.exception.EntityNotFoundException;
import com.c174.exception.NoBodyException;
import com.c174.models.user.UserRequest;
import com.c174.models.user.UserResponse;
import com.c174.services.abstraccion.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User API")
public class UserController {
    @Autowired
    private UserService userService;
    @Operation(summary = "Get all Users - admin only")
    @GetMapping
    public ResponseEntity<?> getAll() throws EntityNotFoundException {
        Map<String,Object> bodyResponse = new HashMap<>();

        List<UserResponse> response = userService.getAllUsers();
        bodyResponse.put("data", response);
        bodyResponse.put("success", Boolean.TRUE);
        return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
    }

    @Operation(summary = "Get User by Id")
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws EntityNotFoundException {
        Map<String, Object> bodyResponse = new HashMap<>();

        UserResponse response = userService.getUserById(id);
        bodyResponse.put("data", response);
        bodyResponse.put("success", Boolean.TRUE);
        return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
    }

    @Operation(summary = "Get User by Email")
    @GetMapping("/email")
    public ResponseEntity<?> getByEmail(@RequestParam String email) throws EntityNotFoundException {
        Map<String, Object> bodyResponse = new HashMap<>();

        UserResponse response = userService.getUserByEmail(email);
        bodyResponse.put("data", response);
        bodyResponse.put("success", Boolean.TRUE);
        return ResponseEntity.status(HttpStatus.OK).body(bodyResponse);
    }

    @Operation(summary = "Save a new User")
    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody @Valid Optional<UserRequest> user) throws EntityExistsException, NoBodyException {
        Map<String, Object> bodyResponse = new HashMap<>();

        if( user == null || user.isEmpty() ){
            throw new NoBodyException("No se recibio ningun dato");
        }
        UserResponse response = userService.createUser(user.get());
        bodyResponse.put("data", response);
        bodyResponse.put("success", Boolean.TRUE);
        return ResponseEntity.status(HttpStatus.CREATED).body(bodyResponse);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeUser(@RequestBody @Valid UserRequest user) throws EntityExistsException {

        if (user.getProfile() == null) return new ResponseEntity<>("Profile is not valid", HttpStatus.BAD_REQUEST);

        var auth = userService.subscribeUser(user);

        if (auth == null) return new ResponseEntity<>("User email is already in use", HttpStatus.NOT_ACCEPTABLE);

        return new ResponseEntity<>(auth, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam @NotBlank String email, @RequestParam @NotBlank String password) throws EntityNotFoundException {

        var auth = userService.loginUser(email,password);

        if (auth == null) return new ResponseEntity<>("Email is not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(auth, HttpStatus.OK);
    }
}
