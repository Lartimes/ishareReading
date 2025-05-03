package org.ishareReading.bankai.service.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

public interface RagService {


    void importDocuments(Resource resource);

    Flux<ChatResponse> retrieve(ChatClient client , String message);
}
