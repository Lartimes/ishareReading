package org.ishareReading.bankai.service.rag.impl;

import com.alibaba.cloud.ai.model.RerankModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ishareReading.bankai.service.rag.RagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private VectorStore VectorStore;
    private RerankModel rerankModel;

    @Autowired
    public RagServiceImpl(VectorStore VectorStore, RerankModel rerankModel) {
        this.VectorStore = VectorStore;
        this.rerankModel = rerankModel;
    }

    @Override
    public void importDocuments(Resource resource) {
        try {
            DocumentReader reader = new org.springframework.ai.reader.tika.TikaDocumentReader(resource);
            List<Document> documents = reader.get();
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = tokenTextSplitter.apply(documents);
            VectorStore.add(splitDocuments);
        } catch (Exception e) {
            throw new RuntimeException("Document import failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Flux<ChatResponse> retrieve(ChatClient client , String message) {
        return client.prompt(new Prompt("system: retriever document data related to user's context_information"))
                .user(message).stream().chatResponse();
    }

}
