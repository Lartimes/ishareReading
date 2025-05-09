package org.ishareReading.bankai.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.LoginService;
import org.ishareReading.bankai.service.UsersService;
import org.ishareReading.bankai.utils.JWTUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;
    private final UsersService userService;

    private final JWTUtils jwtUtils;
    private final ApplicationEventPublisher eventPublisher;

    public LoginController(LoginService loginService, UsersService userService, JWTUtils jwtUtils, ApplicationEventPublisher eventPublisher) {
        this.loginService = loginService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 登录 邮箱 + 密码 如果有时间可以做一下毫秒级验证用户是否存在 大概是分布式单体布隆过滤器 + redis set/hashmap 来搞
     * <p>
     * 登录
     *
     * @return
     */
    @PostMapping
    public Response login(@RequestBody @Validated Users user) {
        String md5Password = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(md5Password);
        final Users login = userService.login(user);
        String token = jwtUtils.getToken(login.getId(), login.getUserName());
        final HashMap<Object, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("name", login.getUserName());
        System.out.println(login);
        map.put("user", login);
        eventPublisher.publishEvent(login);
        return Response.success(map);
    }


    @PostMapping("/getCode")
    public Response getCode(@RequestBody Map<String, String> map) {
        String code = map.get("code");
        String uuid = map.get("uuid");
        String email = map.get("email");
//        携带uuid的验证码 email做绑定，防刷
        if (!loginService.sendEmailCode(uuid, email, code)) {
            return Response.fail("发送失败");
        }
        return Response.success("发送成功,请耐心等待");
    }

    /**
     * @param response
     * @param uuId
     *
     * @throws IOException
     */
    @GetMapping("/captcha.jpg/{uuId}")
    public void captcha(HttpServletResponse response, @PathVariable String uuId) throws Exception {
        loginService.captcha(response, uuId);
    }

    /**
     * 注册
     *
     * @return
     */
    @PostMapping("/register")
    public Response register(@RequestBody Map<String, Object> map) {
        String emailCode = (String) map.get("emailCode");
        Users user = new Users();

        Map<String, Object> userMap = (Map<String, Object>) map.get("user");
        user.setUserName((String) userMap.get("userName"));
        user.setEmail((String) userMap.get("email"));
        user.setPassword((String) userMap.get("password"));
        check(user.getEmail(), emailCode);
        if (!loginService.register(user)) {
            return Response.fail("注册失败,验证码错误");
        }
        return Response.success("注册成功");
    }

    public Response check(String email,
                          String captchaCode) {
        if (email == null || captchaCode == null) {
            throw new IllegalArgumentException("参数不正确");
        }
        if (!loginService.checkEmailCode(email, captchaCode)) {
            return Response.fail("邮箱验证码错误");
        }
        return Response.success();
    }

    /**
     * 找回密码
     *
     * @param findPWVO
     *
     * @return
     */
    @PostMapping("/findPassword")
    public Response findPassword(@RequestBody @Validated Users findPWVO) {
        if (!loginService.findPassword(findPWVO)) {
            return Response.fail("请重试.");
        }
        return Response.success("找回密码成功");
    }


}
