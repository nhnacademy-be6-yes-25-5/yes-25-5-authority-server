package com.nhnacademy.yes25.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
import com.nhnacademy.yes25.persistance.repository.UserInfoRepository;
import com.nhnacademy.yes25.application.service.TokenInfoDeletionService;

/**
 * TokenInfoDeletionServiceImpl 클래스는 토큰 정보와 사용자 정보의 삭제 기능을 구현합니다.
 *
 * @author Chaesanghui
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TokenInfoDeletionServiceImpl implements TokenInfoDeletionService {

    private final TokenInfoRepository tokenInfoRepository;
    private final UserInfoRepository userInfoRepository;

    /**
     * UUID에 해당하는 토큰 정보를 삭제합니다.
     *
     * @param uuid 삭제할 토큰 정보의 UUID
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteExistingTokenInfo(String uuid) {
        tokenInfoRepository.deleteByUuid(uuid);
    }

    /**
     * UUID에 해당하는 사용자 정보를 삭제합니다.
     *
     * @param uuid 삭제할 사용자 정보의 UUID
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteExistingUserInfo(String uuid) {
        userInfoRepository.deleteByUuid(uuid);
    }
}