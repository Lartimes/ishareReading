package org.ishareReading.bankai.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.UsersService;
import org.ishareReading.bankai.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LogManager.getLogger(TokenInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private UsersService userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("进入preHandle method : {}", request.getRequestURI());
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        if (!jwtUtils.checkToken(request)) {
            Response<Object> err = Response.fail("请登录后再操作");
            response(err, response);
            return false;
        }
        final Long userId = jwtUtils.getUserId(request);
        final Users user = userService.getById(userId);
        if (ObjectUtils.isEmpty(user)) {
            Response<Object> err = Response.fail("用户不存在");
            response(err, response);
            return false;
        }
        LOG.info("放入UserHolder :{}", userId.toString());
        UserHolder.set(userId);
        return true;
    }

    private void response(Response<Object> r, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(objectMapper.writeValueAsString(r));
        response.getWriter().flush();
    }
}
