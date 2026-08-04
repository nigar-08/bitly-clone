package com.url_shortener.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
@Data
public class LoginRequest {
    @NotBlank @Size(max = 50)
    private String username;


    @NotBlank @Size(max = 72)
    private String password;


}
