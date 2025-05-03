package org.ishareReading.bankai.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.service.AgentsService;
import org.ishareReading.bankai.util.SSEUtils;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/agents")
public class AgentsReadingController {

    @Autowired
    private AgentsService agentsService;


    @PostMapping("/readingByName")
    public Flux<ServerSentEvent<String>> readingByName(@RequestParam("sessionId") String sessionId, @RequestParam("agentName") String name,
                                                       @RequestParam("userInput") String userInput,
                                                       HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        Long userId = UserHolder.get();
        userId = 1L;
        Flux<ChatResponse> flux = agentsService.readingByName(sessionId, name, userId, userInput);
        return SSEUtils.result(flux);
    }
}
