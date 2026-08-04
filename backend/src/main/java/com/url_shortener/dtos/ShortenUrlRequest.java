package com.url_shortener.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShortenUrlRequest(
        @NotBlank @Size(max = 2048) String originalUrl
) {
}
