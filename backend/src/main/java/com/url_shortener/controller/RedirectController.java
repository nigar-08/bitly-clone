package com.url_shortener.controller;


import com.url_shortener.models.UrlMapping;
import com.url_shortener.service.UrlMappingService;
import lombok.AllArgsConstructor;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RedirectController {
    private UrlMappingService urlMappingService;
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl){
        UrlMapping urlMapping= urlMappingService.getOriginalUrl(shortUrl);
        if(urlMapping!=null){
            return ResponseEntity.status(302).location(URI.create(urlMapping.getOriginalUrl())).build();

        }else{
            return ResponseEntity.notFound().build();
        }

    }
}
