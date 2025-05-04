package org.ishareReading.bankai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.Sessions;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.SessionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/session")
public class SessionController {
    private final SessionsService sessionsService;

    public SessionController(SessionsService sessionsService) {
        this.sessionsService = sessionsService;
    }

    @GetMapping("/getSessions")
    private Response getSessions() {
        Long userId = UserHolder.get();
        return Response.success(sessionsService.list(new LambdaQueryWrapper<Sessions>()
                .eq(Sessions::getUserId, userId)));
    }

    /**
     * 第一次对话默认先创建会话
     *
     * @param content
     *
     * @return
     */
    @GetMapping("/createSession")
    private Response createSession(@RequestParam String content) {
        Long userId = UserHolder.get();
        Sessions sessions = new Sessions();
        sessions.setUserId(userId);
        sessions.setTitle(content);
        sessions.setCreateAt(LocalDateTime.now());
        sessionsService.save(sessions);
        return Response.success(sessions);
    }
}
