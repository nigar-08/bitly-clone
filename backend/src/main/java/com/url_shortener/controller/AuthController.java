package com.url_shortener.controller;


import com.url_shortener.dtos.LoginRequest;
import com.url_shortener.dtos.RegisterRequest;
import com.url_shortener.models.User;
import com.url_shortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private UserService userService;
     private PasswordEncoder passwordEncoder;
    @PostMapping("/public/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        User user =new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setRole("ROLE_USER");
        userService.registerUser(user);
         user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return ResponseEntity.ok("User Registered Sucessfully ");

    }
    @PostMapping("/public/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
      return   ResponseEntity.ok(userService.authenticateUser(loginRequest));
    }
}
