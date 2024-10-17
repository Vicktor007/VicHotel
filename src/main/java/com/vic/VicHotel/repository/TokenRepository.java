package com.vic.VicHotel.repository;

import com.vic.VicHotel.entity.Token;
import com.vic.VicHotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;


public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);
    Optional<Token> findByTokenAndExpiresAtAfter(String token, Date date);
    Optional<Token> findByIdAndExpiresAtAfter(Long id, Date date);
}
