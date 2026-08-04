package com.url_shortener.repository;

import com.url_shortener.models.UrlMapping;
import com.url_shortener.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortUrl(String shortUrl);

    Optional<UrlMapping> findByShortUrlAndUser(String shortUrl, User user);

    boolean existsByShortUrl(String shortUrl);

    @Modifying
    @Query("update UrlMapping mapping set mapping.clickCount = mapping.clickCount + 1 where mapping.id = :id")
    int incrementClickCount(@Param("id") Long id);

    List<UrlMapping> findByUser(User user);
}
