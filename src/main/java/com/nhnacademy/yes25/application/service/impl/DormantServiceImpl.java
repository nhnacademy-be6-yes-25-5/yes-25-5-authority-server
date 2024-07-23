package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.DormantService;
import com.nhnacademy.yes25.application.service.dto.request.DoorayMessagePayload;
import com.nhnacademy.yes25.application.service.dto.request.UnlockDormantRequest;
import com.nhnacademy.yes25.common.exception.ApplicationException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.infrastructure.adaptor.DoorayAdaptor;
import com.nhnacademy.yes25.infrastructure.adaptor.UserAdaptor;
import com.nhnacademy.yes25.presentation.dto.request.CreateAuthNumberRequest;
import com.nhnacademy.yes25.presentation.dto.request.SubmitAuthNumberRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DormantServiceImpl implements DormantService {

    private final DoorayAdaptor doorayAdaptor;
    private final UserAdaptor userAdaptor;
    private final RedissonClient redissonClient;
    private final SecureRandom random;

    @Override
    public void createAuthNumberByEmail(CreateAuthNumberRequest request) {
        String email = request.email();
        String authNumber = generateAuthNumber();

        RBucket<String> bucket = redissonClient.getBucket(email);
        bucket.set(authNumber, 3, TimeUnit.MINUTES);

        DoorayMessagePayload payload = DoorayMessagePayload.from(authNumber);

        doorayAdaptor.sendAuthNumber(payload);
    }

    private boolean checkAuthNumberByRequest(SubmitAuthNumberRequest request) {
        RBucket<String> bucket = redissonClient.getBucket(request.email());
        String storedAuthNumber = bucket.get();

        if (Objects.isNull(storedAuthNumber)) {
            throw new ApplicationException(
                ErrorStatus.toErrorStatus("인증번호가 만료되었습니다.", 401, LocalDateTime.now()));
        }

        return storedAuthNumber.equals(request.verificationCode());
    }

    @Override
    public Boolean updateUserStateByAuthNumber(SubmitAuthNumberRequest request) {
        boolean isEqualAuthNumber = checkAuthNumberByRequest(request);

        if (isEqualAuthNumber) {
            UnlockDormantRequest unlockDormantRequest = UnlockDormantRequest.from(request.email());
            userAdaptor.unLockDormantState(unlockDormantRequest);
        }

        return isEqualAuthNumber;
    }

    private String generateAuthNumber() {
        int number = random.nextInt(90000) + 10000;

        return String.valueOf(number);
    }
}
