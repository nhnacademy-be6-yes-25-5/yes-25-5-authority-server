package com.nhnacademy.yes25.persistance.repository;

import com.nhnacademy.yes25.persistance.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByUuid(String uuid);

    Optional<UserInfo> findByCustomerId(Long customerId);

    List<UserInfo> findAllByCustomerId(Long customerId);

    void deleteByUuid(String uuid);

}
