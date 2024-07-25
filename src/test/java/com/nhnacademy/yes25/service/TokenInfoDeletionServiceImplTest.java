//package com.nhnacademy.yes25.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TokenInfoDeletionServiceImplTest {
//
//    @Mock
//    private TokenInfoRepository tokenInfoRepository;
//
//    @Mock
//    private UserInfoRepository userInfoRepository;
//
//    @InjectMocks
//    private TokenInfoDeletionServiceImpl tokenInfoDeletionService;
//
//    @Test
//    @DisplayName("토큰 정보 삭제 - 성공")
//    void deleteExistingTokenInfo_success() {
//        // given
//        String uuid = "test-uuid";
//
//        // when
//        tokenInfoDeletionService.deleteExistingTokenInfo(uuid);
//
//        // then
//        verify(tokenInfoRepository, times(1)).deleteByUuid(uuid);
//    }
//
//    @Test
//    @DisplayName("사용자 정보 삭제 - 성공")
//    void deleteExistingUserInfo_success() {
//        // given
//        String uuid = "test-uuid";
//
//        // when
//        tokenInfoDeletionService.deleteExistingUserInfo(uuid);
//
//        // then
//        verify(userInfoRepository, times(1)).deleteByUuid(uuid);
//    }
//
//    @Test
//    @DisplayName("토큰 정보 삭제 - 트랜잭션 전파 확인")
//    void deleteExistingTokenInfo_transactionPropagation() throws NoSuchMethodException {
//        // given
//        String uuid = "test-uuid";
//
//        // when
//        tokenInfoDeletionService.deleteExistingTokenInfo(uuid);
//
//        // then
//        // 트랜잭션 전파 설정 확인 (메타데이터 확인)
//        Transactional transactional = TokenInfoDeletionServiceImpl.class.getMethod("deleteExistingTokenInfo", String.class).getAnnotation(Transactional.class);
//        assert transactional != null;
//        assert transactional.propagation() == Propagation.REQUIRES_NEW;
//    }
//
//    @Test
//    @DisplayName("사용자 정보 삭제 - 트랜잭션 전파 확인")
//    void deleteExistingUserInfo_transactionPropagation() throws NoSuchMethodException {
//        // given
//        String uuid = "test-uuid";
//
//        // when
//        tokenInfoDeletionService.deleteExistingUserInfo(uuid);
//
//        // then
//        // 트랜잭션 전파 설정 확인 (메타데이터 확인)
//        Transactional transactional = TokenInfoDeletionServiceImpl.class.getMethod("deleteExistingUserInfo", String.class).getAnnotation(Transactional.class);
//
//        // Add assertion
//        assertNotNull(transactional, "Transactional annotation should be present");
//        assertEquals(Propagation.REQUIRES_NEW, transactional.propagation(), "Propagation should be REQUIRES_NEW");
//    }
//}