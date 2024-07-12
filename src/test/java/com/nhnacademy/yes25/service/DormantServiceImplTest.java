package com.nhnacademy.yes25.service;

import com.nhnacademy.yes25.application.service.dto.request.DoorayMessagePayload;
import com.nhnacademy.yes25.application.service.dto.request.UnlockDormantRequest;
import com.nhnacademy.yes25.common.exception.ApplicationException;
import com.nhnacademy.yes25.infrastructure.adaptor.DoorayAdaptor;
import com.nhnacademy.yes25.infrastructure.adaptor.UserAdaptor;
import com.nhnacademy.yes25.presentation.dto.request.CreateAuthNumberRequest;
import com.nhnacademy.yes25.presentation.dto.request.SubmitAuthNumberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import com.nhnacademy.yes25.application.service.impl.DormantServiceImpl;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DormantServiceImplTest {

    @Mock
    private DoorayAdaptor doorayAdaptor;

    @Mock
    private UserAdaptor userAdaptor;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private SecureRandom secureRandom;

    @InjectMocks
    private DormantServiceImpl dormantService;

    @DisplayName("이메일로 인증번호 생성 및 전송 - 성공")
    @Test
    void createAuthNumberByEmail_success() {
        // given
        CreateAuthNumberRequest request = new CreateAuthNumberRequest("test@example.com");
        RBucket<String> bucket = mock(RBucket.class);
        doReturn(bucket).when(redissonClient).getBucket(any(String.class));
        doNothing().when(bucket).set(any(String.class), eq(3L), eq(TimeUnit.MINUTES));
        when(secureRandom.nextInt(90000)).thenReturn(123456);

        // when
        dormantService.createAuthNumberByEmail(request);

        // then
        verify(bucket, times(1)).set(any(), eq(3L), eq(TimeUnit.MINUTES));
        verify(doorayAdaptor, times(1)).sendAuthNumber(any(DoorayMessagePayload.class));
    }

    @DisplayName("인증번호 확인 - 성공")
    @Test
    void updateUserStateByAuthNumber_success() {
        // given
        SubmitAuthNumberRequest request = new SubmitAuthNumberRequest("test@example.com", "123456");
        RBucket<String> bucket = mock(RBucket.class);
        doReturn(bucket).when(redissonClient).getBucket(any(String.class));
        doReturn("123456").when(bucket).get();

        // when
        Boolean result = dormantService.updateUserStateByAuthNumber(request);

        // then
        assertTrue(result);
        verify(userAdaptor, times(1)).unLockDormantState(any(UnlockDormantRequest.class));
    }

    @DisplayName("인증번호 확인 - 실패 (인증번호 만료)")
    @Test
    void updateUserStateByAuthNumber_failure_expiredAuthNumber() {
        // given
        SubmitAuthNumberRequest request = new SubmitAuthNumberRequest("test@example.com", "123456");
        RBucket<String> bucket = mock(RBucket.class);
        doReturn(bucket).when(redissonClient).getBucket(any(String.class));
        doReturn(null).when(bucket).get();

        // then
        assertThrows(ApplicationException.class, () -> dormantService.updateUserStateByAuthNumber(request));
    }

    @DisplayName("인증번호 확인 - 실패 (인증번호 불일치)")
    @Test
    void updateUserStateByAuthNumber_failure_invalidAuthNumber() {
        // given
        SubmitAuthNumberRequest request = new SubmitAuthNumberRequest("test@example.com", "654321");
        RBucket<String> bucket = mock(RBucket.class);
        doReturn(bucket).when(redissonClient).getBucket(any(String.class));
        doReturn("123456").when(bucket).get();

        // when
        Boolean result = dormantService.updateUserStateByAuthNumber(request);

        // then
        assertFalse(result);
        verify(userAdaptor, never()).unLockDormantState(any(UnlockDormantRequest.class));
    }
}