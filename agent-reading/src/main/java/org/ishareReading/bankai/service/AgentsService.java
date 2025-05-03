package org.ishareReading.bankai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ishareReading.bankai.model.Agents;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * <p>
 * agent表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
public interface AgentsService extends IService<Agents> {

    Map<Agents, ChatModel> getAgentsByUserId(Long userId);

    void registerAgentsByUserId(Long userId);

    Agents getAgentsByAgentName(String name , Long userId);

    Flux<ChatResponse> readingByName(String sessionId, String name, Long userId , String userInput);

}
