package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.service.LoginService;
import org.ishareReading.bankai.service.UsersService;
import org.ishareReading.bankai.utils.EmailSenderUtil;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Random;

@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger LOG = LogManager.getLogger(LoginServiceImpl.class);
    private final EmailSenderUtil emailSenderUtil;
    private final RedisCacheUtil redisCacheUtil;
    private final UsersService userService;
    @Resource
    private Producer captchaProducer;

    public LoginServiceImpl(EmailSenderUtil emailSenderUtil, RedisCacheUtil redisCacheUtil, UsersService userService) {
        this.emailSenderUtil = emailSenderUtil;
        this.redisCacheUtil = redisCacheUtil;
        this.userService = userService;
    }


    @Override
    public void captcha(HttpServletResponse response, String uuId) throws IOException {
        if (ObjectUtils.isEmpty(uuId)) {
            LOG.warn("UUId is null");
            throw new IllegalArgumentException("uuId is null");
        }
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        final BufferedImage bufferedImage = generateGraphCaptcha(uuId);
        final ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        IOUtils.closeQuietly(outputStream);
        LOG.info("获取验证码成功. UUID : {} ,image : {}", uuId, bufferedImage.toString());
    }

    private BufferedImage generateGraphCaptcha(String uuId) {
        String code = captchaProducer.createText();
        redisCacheUtil.set(RedisConstant.CAPTCHA + uuId, code, 5 * 60);
        return captchaProducer.createImage(code);
    }

    @Override
    public Boolean sendEmailCode(String uuid, String email, String code) {
        if (redisCacheUtil.hashKey(email)) {
            throw new BusinessException("请一分钟后重新发送验证码");
        }
        String captcha = redisCacheUtil.getKey(RedisConstant.CAPTCHA + uuid);
        if (captcha == null) {
            LOG.error("不存在该验证码");
            throw new BusinessException("请重新刷新验证码");
        }
        if (!Objects.equals(code, captcha)) {
            LOG.error("验证码不正确:{}", code);
            throw new BusinessException("验证码不正确");
        }

        final String verificationCode = generateVerificationCode(6);
        LOG.info("发送邮箱验证码 TO : {} , CODE : {}", email, verificationCode);
        try {
            emailSenderUtil.sentTo(email, verificationCode);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new BusinessException("邮箱发送失败");
        }
        redisCacheUtil.set(email, verificationCode, 60);
        return Boolean.TRUE;
    }

    @Override
    public boolean checkEmailCode(String email, String emailCode) {
        if (!redisCacheUtil.hashKey(email)) {
            LOG.error("邮箱验证码失效");
            throw new BusinessException("邮箱验证码失效");
        }
        final String code = redisCacheUtil.getKey(email);
        if (!code.equals(emailCode)) {
            LOG.error("验证码错误");
            throw new BusinessException("验证码错误");
        }
        redisCacheUtil.deleteKey(email);
        LOG.info("邮箱验证码匹配成功，删除邮箱验证码 : {}", email);
        return true;
    }

    @Override
    public boolean register(Users users) {
        final String email = users.getEmail();
        Users existUser = userService.getBaseMapper().selectOne(new LambdaQueryWrapper<Users>().eq(StringUtils.hasText(email), Users::getEmail, email));
        if (existUser != null) {
            LOG.info("该邮箱已经被注册 : {}", email);
            throw new BusinessException("该邮箱已经被注册");
        }
        final String md5Password = DigestUtils.md5Hex(users.getPassword());
        users.setPassword(md5Password);
        userService.save(users);
        LOG.info("注册账户 user : {}", users);
        return true;
    }

    @Transactional
    @Override
    public boolean findPassword(Users findPWVO) {
        final String email = findPWVO.getEmail();
        Users user = userService.getOne(new LambdaQueryWrapper<Users>()
                .eq(StringUtils.hasText(email), Users::getEmail, email));
        if (ObjectUtils.isEmpty(user)) {
            LOG.info("该邮箱未被注册");
            throw new BusinessException("该邮箱未被注册");
        }
        final String password =
                DigestUtils.md5Hex(findPWVO.getPassword());
        user.setPassword(password);
        LOG.info("更新密码: {}", user);
        final String key = redisCacheUtil.getKey(email);
        if (!StringUtils.hasText(key)) {
            LOG.info("邮箱验证码过期");
        } else {
            redisCacheUtil.deleteKey(email);
            LOG.info("删除邮箱验证码 : {}", email);
        }
        LOG.info("删除验证码，找回密码");
        return userService.updateById(user);
    }

    private static String generateVerificationCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }


}
