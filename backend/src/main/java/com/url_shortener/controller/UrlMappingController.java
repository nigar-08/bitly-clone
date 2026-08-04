package com.url_shortener.controller;


import com.url_shortener.dtos.ClickEventDTO;
import com.url_shortener.dtos.UrlMappingDTO;
import com.url_shortener.dtos.ShortenUrlRequest;
import com.url_shortener.models.ClickEvent;
import com.url_shortener.models.UrlMapping;
import com.url_shortener.models.User;
import com.url_shortener.service.UrlMappingService;
import com.url_shortener.service.UserService;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {
    private UrlMappingService urlMappingService;
    private UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> createShortUrl(@Valid @RequestBody ShortenUrlRequest request,
                                                        Principal principal){
         User user= userService.findByUsername(principal.getName());
          UrlMappingDTO urlMappingDTO= urlMappingService.createShortUrl(request.originalUrl(),user);
          return ResponseEntity.ok(urlMappingDTO);

    }
    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal){
        User user= userService.findByUsername(principal.getName());
        List<UrlMappingDTO> urls =urlMappingService.getUrlsByUser(user);
        return  ResponseEntity.ok(urls);
    }
    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> getUserUrlAnalytics(@PathVariable String shortUrl,
                                                                   @RequestParam("startDate") String startDate,
                                                                   @RequestParam("endDate") String endDate,
                                                                   Principal principal){
        DateTimeFormatter formatter=DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start=LocalDateTime.parse(startDate,formatter);
        LocalDateTime end=LocalDateTime.parse(endDate,formatter);
        User user = userService.findByUsername(principal.getName());
        List<ClickEventDTO> clickEventDTOS=urlMappingService.getClickEventsByDate(shortUrl,user,start,end);
        return ResponseEntity.ok(clickEventDTOS);



    }
    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate,Long>> getTotalClicksByDate(Principal principal,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate){
        DateTimeFormatter formatter=DateTimeFormatter.ISO_LOCAL_DATE;
        User user=userService.findByUsername(principal.getName());
        LocalDate start=LocalDate.parse(startDate,formatter);
        LocalDate end=LocalDate.parse(endDate,formatter);
        Map<LocalDate,Long>   totalClicks=urlMappingService.getTotalClicksByUserAndDate(user,start,end);
        return ResponseEntity.ok(totalClicks);
    }
}
