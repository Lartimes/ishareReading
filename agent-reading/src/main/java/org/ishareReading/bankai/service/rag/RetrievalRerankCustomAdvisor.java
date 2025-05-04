package org.ishareReading.bankai.service.rag;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.model.RerankModel;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.vectorstore.VectorStore;

public class RetrievalRerankCustomAdvisor extends RetrievalRerankAdvisor {
    public RetrievalRerankCustomAdvisor(VectorStore vectorStore, RerankModel rerankModel) {
        super(vectorStore, rerankModel);
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

        return super.aroundCall(advisedRequest, chain);
    }
}
