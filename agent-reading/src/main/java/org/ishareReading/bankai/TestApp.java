package org.ishareReading.bankai;

import org.ishareReading.bankai.model.Agents;
import org.ishareReading.bankai.service.AgentsService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@MapperScan(basePackages = {"org.ishareReading.bankai.mapper"})
@SpringBootApplication(scanBasePackages = {"org.ishareReading"})
public class TestApp implements CommandLineRunner {
    @Autowired
    private AgentsService agentsService;

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        agentsService.registerAgentsByUserId(1L);
        Map<Agents, ChatModel> agentsByUserId = agentsService.getAgentsByUserId(1L);
        System.out.println(agentsByUserId);
        agentsByUserId.forEach(( k ,v) -> System.out.println(k + ":" + v));
    }
}
