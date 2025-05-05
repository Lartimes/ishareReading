package org.ishareReading.bankai;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.ishareReading.bankai.model.Agents;
import org.ishareReading.bankai.service.AgentsService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StartUpListener implements CommandLineRunner {

    @Autowired
    private AgentsService agentsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Override
    public void run(String... args) {
        agentsService.registerAgentsByUserId(1L);
        Map<Agents, ChatModel> agentsByUserId = agentsService.getAgentsByUserId(1L);
        System.out.println(agentsByUserId);
        agentsByUserId.forEach((k, v) -> System.out.println(k + ":" + v));
        System.out.println(redisHost);
        System.out.println(redisTemplate);
        System.out.println(redisTemplate.getClientList());
        System.out.println(elasticsearchClient);
    }

}
