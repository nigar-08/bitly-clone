package com.url_shortener.service;

import com.url_shortener.exception.InvalidUrlException;
import com.url_shortener.exception.ResourceNotFoundException;
import com.url_shortener.models.ClickEvent;
import com.url_shortener.models.UrlMapping;
import com.url_shortener.models.User;
import com.url_shortener.repository.ClickEventRepository;
import com.url_shortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlMappingServiceTest {
    private UrlMappingRepository mappings;
    private ClickEventRepository clicks;
    private SecureRandom random;
    private UrlMappingService service;

    @BeforeEach
    void setUp() {
        mappings = mock(UrlMappingRepository.class);
        clicks = mock(ClickEventRepository.class);
        random = mock(SecureRandom.class);
        Clock clock = Clock.fixed(Instant.parse("2026-08-04T06:00:00Z"), ZoneOffset.UTC);
        service = new UrlMappingService(mappings, clicks, random, clock);
    }

    @Test
    void rejectsNonHttpUrlsBeforePersistence() {
        assertThatThrownBy(() -> service.createShortUrl("javascript:alert(1)", new User()))
                .isInstanceOf(InvalidUrlException.class);
        verify(mappings, never()).save(any());
    }

    @Test
    void retriesWhenGeneratedCodeAlreadyExists() {
        when(random.nextInt(62)).thenReturn(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1);
        when(mappings.existsByShortUrl("AAAAAAAA")).thenReturn(true);
        when(mappings.existsByShortUrl("BBBBBBBB")).thenReturn(false);
        when(mappings.save(any())).thenAnswer(invocation -> {
            UrlMapping mapping = invocation.getArgument(0);
            mapping.setId(42L);
            return mapping;
        });
        User owner = new User();
        owner.setUsername("owner");

        assertThat(service.createShortUrl("https://example.com/article", owner).getShortUrl())
                .isEqualTo("BBBBBBBB");
    }

    @Test
    void redirectAtomicallyIncrementsCounterAndRecordsClick() {
        UrlMapping mapping = new UrlMapping();
        mapping.setId(7L);
        mapping.setShortUrl("abc123XY");
        mapping.setOriginalUrl("https://example.com");
        when(mappings.findByShortUrl("abc123XY")).thenReturn(Optional.of(mapping));

        assertThat(service.getOriginalUrl("abc123XY").getOriginalUrl()).isEqualTo("https://example.com");
        verify(mappings).incrementClickCount(7L);
        ArgumentCaptor<ClickEvent> captor = ArgumentCaptor.forClass(ClickEvent.class);
        verify(clicks).save(captor.capture());
        assertThat(captor.getValue().getClickDate()).isEqualTo(LocalDateTime.of(2026, 8, 4, 6, 0));
        assertThat(captor.getValue().getUrlMapping()).isSameAs(mapping);
    }

    @Test
    void analyticsDoesNotExposeAnotherUsersShortUrl() {
        User requester = new User();
        when(mappings.findByShortUrlAndUser("private01", requester)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getClickEventsByDate(
                "private01", requester,
                LocalDateTime.of(2026, 8, 1, 0, 0),
                LocalDateTime.of(2026, 8, 2, 0, 0)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
