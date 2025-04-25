package org.ishareReading.bankai.service;

import jakarta.servlet.http.HttpServletResponse;
import org.ishareReading.bankai.model.Users;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface LoginService {

    void captcha(HttpServletResponse response, String uuId) throws IOException;

    Boolean sendEmailCode(String uuid, String email, String code);

    boolean checkEmailCode(String email, String captchaCode);

    boolean register(Users users);

    @Transactional
    boolean findPassword(Users findPWVO);
}
