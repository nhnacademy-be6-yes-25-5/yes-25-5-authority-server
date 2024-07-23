package com.nhnacademy.yes25.persistance.repository;

import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenInfoRepository extends JpaRepository<TokenInfo, Long> {

    Optional<TokenInfo> findByUuid(String uuid);

    Optional<TokenInfo> findByRefreshToken(String refreshToken);

    void deleteByUuid(String uuid);

    List<TokenInfo> findAllByExpiryDateBefore(LocalDateTime date);

}
