package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.presentation.dto.request.CreateAuthNumberRequest;
import com.nhnacademy.yes25.presentation.dto.request.SubmitAuthNumberRequest;

public interface DormantService {

    void createAuthNumberByEmail(CreateAuthNumberRequest request);

    Boolean updateUserStateByAuthNumber(SubmitAuthNumberRequest request);
}
