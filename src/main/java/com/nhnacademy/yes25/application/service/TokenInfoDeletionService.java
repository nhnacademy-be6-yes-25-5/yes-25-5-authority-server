package com.nhnacademy.yes25.application.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface TokenInfoDeletionService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteExistingTokenInfo(String uuid);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteExistingUserInfo(String uuid);
}