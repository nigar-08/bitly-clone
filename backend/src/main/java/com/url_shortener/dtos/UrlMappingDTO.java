package com.url_shortener.dtos;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UrlMappingDTO {
    private long id;
    private int clickCount;
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime createdDate;
    private String username;


}
