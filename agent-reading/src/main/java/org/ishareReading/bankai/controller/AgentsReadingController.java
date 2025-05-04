package org.ishareReading.bankai.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.AgentsService;
import org.ishareReading.bankai.util.SSEUtils;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/agents")
public class AgentsReadingController {

    private final AgentsService agentsService;

    public AgentsReadingController(AgentsService agentsService) {
        this.agentsService = agentsService;
    }


    @PostMapping("/readingByName")
    public Flux<ServerSentEvent<String>> readingByName(@RequestParam("sessionId") String sessionId, @RequestParam("agentName") String name,
                                                       @RequestParam("userInput") String userInput,
                                                       HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        Long userId = UserHolder.get();
        Flux<ChatResponse> flux = agentsService.readingByName(sessionId, name, userId, userInput);
        return SSEUtils.result(flux);
    }


    @GetMapping("/getAgents")
    public Response getAgenstByName(@RequestParam("name") String name) {
        Long userId = UserHolder.get();
        return Response.success(agentsService.getAgentsByAgentName(name , userId));
    }
}
