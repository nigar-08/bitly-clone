package com.url_shortener.dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
@Data
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 50)
    private String username;
    @NotBlank @Email @Size(max = 254)
    private String email;
    private Set<String> role;
    @NotBlank @Size(min = 8, max = 72)
    private String password;


}
