package org.ishareReading.bankai.service.impl;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ishareReading.bankai.config.PromptConfig;
import org.ishareReading.bankai.mapper.AgentsMapper;
import org.ishareReading.bankai.model.Agents;
import org.ishareReading.bankai.model.ChatModelConfig;
import org.ishareReading.bankai.service.AgentsService;
import org.ishareReading.bankai.service.factory.ChatModelFactory;
import org.ishareReading.bankai.service.memory.MultiLevelMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * <p>
 * agent表 服务实现类
 * </p>
 */
@Service
public class AgentsServiceImpl extends ServiceImpl<AgentsMapper, Agents> implements AgentsService, BeanPostProcessor {

    private final PromptConfig promptConfig;
    private final ObjectMapper objectMapper;
    private final MultiLevelMemory multiLevelMemory;
    private final PgVectorStore vectorStore;
    private final DashScopeRerankModel rerankModel;
    private ChatModelConfig chatModelConfig;
    @Value("${spring.ai.openai.api-key:sk-9f6e57e4b29540e2958f0a57f71fa143}")
    private String apiKey;
    @Value("${spring.ai.openai.base-url:https://api.deepseek.com}")
    private String baseUrl;

    public AgentsServiceImpl(PgVectorStore vectorStore, PromptConfig promptConfig, ObjectMapper objectMapper,
                             MultiLevelMemory multiLevelMemory, DashScopeRerankModel rerankModel) {
        this.vectorStore = vectorStore;
        this.promptConfig = promptConfig;
        this.objectMapper = objectMapper;
        this.multiLevelMemory = multiLevelMemory;
        this.rerankModel = rerankModel;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //        默认先用deepseek
//        应该提供一些用户或管理端的方法，修改模型参数等
        this.chatModelConfig = new ChatModelConfig();
        chatModelConfig.setApiKey(apiKey);
        chatModelConfig.setBaseUrl(baseUrl);
        chatModelConfig.setModel("deepseek-chat");
        chatModelConfig.setEnabled(true);
        chatModelConfig.setTemperature(1.5);
        chatModelConfig.setFrequencyPenalty(0.9);
        chatModelConfig.setPresencePenalty(0.9);
        chatModelConfig.setTopP(0.75);
        chatModelConfig.setModelType(ChatModelFactory.ModelType.OPENAI);
        chatModelConfig.setDescription("书籍阅读小助手");
        chatModelConfig.setMaxTokens(4000);
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /**
     * 根据userId 获取其拥有的Agents
     *
     * @param userId
     *
     * @return
     */
    @Override
    public Map<Agents, ChatModel> getAgentsByUserId(Long userId) {
        Map<Agents, ChatModel> result = new HashMap<>();
        List<Agents> agentsList = this.list(new LambdaQueryWrapper<Agents>()
                .eq(Agents::getUserId, userId));
        agentsList.forEach(agents -> {
            Object modelConfig = agents.getModelConfig();
            ChatModelConfig chatModelConfig = null;
            try {
                chatModelConfig = objectMapper.readValue(modelConfig.toString(), ChatModelConfig.class);
                result.put(agents, ChatModelFactory.createDefaultModel(chatModelConfig));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }


    /**
     * 分配一个ChatAgent 还有----------> 默认 登录之后为其初始化默认三个Agents 这个最后要写到logListener
     */
    @Transactional
    @Override
    public void registerAgentsByUserId(Long userId) {
        long count = this.count(new LambdaQueryWrapper<Agents>()
                .eq(Agents::getUserId, userId));
        if (count >= 3) {
            System.out.println("该用户已经注册");
            return;
        }
        ArrayList<Agents> agents = new ArrayList<>();
        Agents agents1 = new Agents();
        agents1.setUserId(userId);
        agents1.setWelcomeMessage("我是书籍欣赏者小助手哦😘😘😘");
        agents1.setName("appreciator");
        agents1.setModelConfig(chatModelConfig);
        agents1.setSystemPrompt(promptConfig.appreciatorTemplate().getTemplate());
        agents.add(agents1);
        Agents agents2 = new Agents();
        agents2.setUserId(userId);
        agents2.setWelcomeMessage("我是书籍辨证者小助手哦😒😒😒");
        agents2.setName("dialectician");
        agents2.setModelConfig(chatModelConfig);
        agents2.setSystemPrompt(promptConfig.dialecticianTemplate().getTemplate());
        agents.add(agents2);
        Agents agents3 = new Agents();
        agents3.setUserId(userId);
        agents3.setWelcomeMessage("我是书籍中立者小助手哦😊😊😊");
        agents3.setName("neutralist");
        agents3.setModelConfig(chatModelConfig);
        agents3.setSystemPrompt(promptConfig.neutralistTemplate().getTemplate());
        agents.add(agents3);
        this.saveBatch(agents);
    }


    @Override
    public Agents getAgentsByAgentName(String name, Long userId) {
        return this.getOne(new LambdaQueryWrapper<Agents>()
                .eq(Agents::getUserId, userId)
                .eq(Agents::getName, name)
                .last("limit 1"));
    }

    @Override
    public Flux<ChatResponse> readingByName(String sessionId, String name, Long userId, String userInput) {
        Map<Agents, ChatModel> agentsByUserId = getAgentsByUserId(userId);
        Set<Map.Entry<Agents, ChatModel>> entries = agentsByUserId.entrySet();
        ChatClient client = null;
        Agents targetAgent = null;
        for (Map.Entry<Agents, ChatModel> entry : entries) {
            targetAgent = entry.getKey();
            if (targetAgent.getName().equals(name)) {
                ChatModel chatModel = entry.getValue();
//                todo 重排序， 向量双路召回 ， 知识库
                client = ChatClient.builder(chatModel).defaultSystem(promptConfig.systemPromptTemplate().getTemplate()).build();
                break;
            }
        }
        String advise = """
                Context information is below.
                ---------------------
                {question_answer_context}
                ---------------------
                """;
        System.out.println("=============================");
        SearchRequest searchRequest = SearchRequest.builder().query(userInput).build();
        assert targetAgent != null;
        assert client != null;
        return client.prompt(targetAgent.getSystemPrompt())
                .user(userInput)
                .advisors(new MessageChatMemoryAdvisor(multiLevelMemory, sessionId, 20))
                .advisors(new RetrievalRerankAdvisor(vectorStore, rerankModel, searchRequest, advise, 0.1),
                        new SimpleLoggerAdvisor())
                .stream().chatResponse();
    }


}
