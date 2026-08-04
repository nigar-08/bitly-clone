package com.url_shortener.service;


import com.url_shortener.dtos.ClickEventDTO;
import com.url_shortener.dtos.UrlMappingDTO;
import com.url_shortener.models.ClickEvent;
import com.url_shortener.models.UrlMapping;
import com.url_shortener.models.User;
import com.url_shortener.repository.ClickEventRepository;
import com.url_shortener.repository.UrlMappingRepository;
import com.url_shortener.exception.InvalidUrlException;
import com.url_shortener.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UrlMappingService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 8;
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;
    private final SecureRandom secureRandom;
    private final Clock clock;

    public UrlMappingService(UrlMappingRepository urlMappingRepository, ClickEventRepository clickEventRepository,
                             SecureRandom secureRandom, Clock clock) {
        this.urlMappingRepository = urlMappingRepository;
        this.clickEventRepository = clickEventRepository;
        this.secureRandom = secureRandom;
        this.clock = clock;
    }

    @Transactional
    public UrlMappingDTO createShortUrl(String originalUrl, User user) {
        validateOriginalUrl(originalUrl);
        String shortUrl=generateShortUrl();
        UrlMapping urlMapping=new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now(clock));
        UrlMapping savedUrlMapping=urlMappingRepository.save(urlMapping);
        return convertToDto(savedUrlMapping);

    }
    private UrlMappingDTO convertToDto(UrlMapping urlMapping) {
        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        urlMappingDTO.setUsername(urlMapping.getUser().getUsername());// if exists

        return urlMappingDTO;
    }


    private String generateShortUrl() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            StringBuilder shortUrl = new StringBuilder(SHORT_CODE_LENGTH);
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                shortUrl.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
            }
            String candidate = shortUrl.toString();
            if (!urlMappingRepository.existsByShortUrl(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not allocate a unique short code");
    }

    public List<UrlMappingDTO> getUrlsByUser(User user) {
        return urlMappingRepository.findByUser(user).stream()
                .map(this::convertToDto).toList();
    }

    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, User user, LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new InvalidUrlException("endDate must not be before startDate");
        }
        UrlMapping urlMapping = urlMappingRepository.findByShortUrlAndUser(shortUrl, user)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));
            return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, start, end).stream()
                    .collect(Collectors.groupingBy(
                            click -> click.getClickDate().toLocalDate(),
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .map(entry -> {
                        ClickEventDTO clickEventDTO = new ClickEventDTO();
                        clickEventDTO.setClickDate(entry.getKey());
                        clickEventDTO.setCount(entry.getValue());
                        return clickEventDTO;
                    })
                    .sorted((left, right) -> left.getClickDate().compareTo(right.getClickDate()))
                    .collect(Collectors.toList());
    }


    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new InvalidUrlException("endDate must not be before startDate");
        }
        List<UrlMapping> urlMappings=urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents=clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings ,start.atStartOfDay(),end.plusDays(1).atStartOfDay());
        return clickEvents.stream()
                .collect(Collectors.groupingBy(click->click.getClickDate().toLocalDate(),
                        Collectors.counting()));
    }

    @Transactional
    public UrlMapping getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl).orElse(null);
        if(urlMapping!=null){
            urlMappingRepository.incrementClickCount(urlMapping.getId());
            ClickEvent clickEvent=new ClickEvent();
            clickEvent.setClickDate(LocalDateTime.now(clock));
            clickEvent.setUrlMapping(urlMapping);
            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }

    private void validateOriginalUrl(String originalUrl) {
        try {
            URI uri = new URI(originalUrl);
            String scheme = uri.getScheme();
            if (scheme == null || uri.getHost() == null
                    || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException("originalUrl must be an absolute HTTP or HTTPS URL");
            }
        } catch (URISyntaxException exception) {
            throw new InvalidUrlException("originalUrl is malformed");
        }
    }
}
