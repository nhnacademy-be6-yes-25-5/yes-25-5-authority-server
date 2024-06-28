package com.nhnacademy.yes25.persistance.repository;

import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenInfoRepository extends JpaRepository<TokenInfo, Long> {

    Optional<TokenInfo> findByUuid(String uuid);

    Optional<TokenInfo> findByCustomerId(Long customerId);

    void deleteByUuid(String accessTokenUuid);

    void deleteByCustomerId(Long customerId);

}
