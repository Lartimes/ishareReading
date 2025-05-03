package org.ishareReading.bankai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public class SSEUtils {
    private SSEUtils() {

    }
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static Flux<ServerSentEvent<String>> result(Flux<ChatResponse> responseFlux) {
        return responseFlux.map(response -> ServerSentEvent.builder(toJson(response))
                .event("message")
                .build());
    }

    /**
     * 将流式回答结果转json字符串
     *
     * @param chatResponse 流式回答结果
     *
     * @return String json字符串
     */
    @SneakyThrows
    public static String toJson(ChatResponse chatResponse) {
        return objectMapper.writeValueAsString(chatResponse);
    }
}
