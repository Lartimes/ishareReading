package org.ishareReading.bankai.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreProperties;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 解决多个type冲突问题
 */
@Configuration
public class VectorStoreAndRerankConfig {


    @Value("${spring.ai.dashscope.embedding.api-key}")
    private String apiKey;

    @Value("${spring.ai.dashscope.rerank.options.model}")
    private String rerankModel;
    @Value("${spring.ai.dashscope.embedding.options.model}")
    private String vectorModel;

    @Bean
    public DashScopeRerankModel rerankModel() {
        DashScopeRerankOptions build = DashScopeRerankOptions.builder().build();
        build.setModel(rerankModel);
        build.setTopN(5);
        build.setReturnDocuments(true);
        return new DashScopeRerankModel(new DashScopeApi(apiKey), build);
    }

    /**
     * 手动注册pgvector pgvector
     *
     * @return
     */
    @Primary
    @Bean
    public PgVectorStore vectorStore(JdbcTemplate jdbcTemplate,
                                     PgVectorStoreProperties properties, ObjectProvider<ObservationRegistry> observationRegistry,
                                     ObjectProvider<VectorStoreObservationConvention> customObservationConvention,
                                     BatchingStrategy batchingStrategy) {

        var initializeSchema = properties.isInitializeSchema();
        return PgVectorStore.builder(jdbcTemplate, embeddingModel())
                .schemaName(properties.getSchemaName())
                .idType(properties.getIdType())
                .vectorTableName(properties.getTableName())
                .vectorTableValidationsEnabled(properties.isSchemaValidation())
                .dimensions(properties.getDimensions())
                .distanceType(properties.getDistanceType())
                .removeExistingVectorStoreTable(properties.isRemoveExistingVectorStoreTable())
                .indexType(properties.getIndexType())
                .initializeSchema(initializeSchema)
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .customObservationConvention(customObservationConvention.getIfAvailable(() -> null))
                .batchingStrategy(batchingStrategy)
                .maxDocumentBatchSize(properties.getMaxDocumentBatchSize())
                .build();
    }

    /**
     * 解决DashScope autoConfiguraion 与Dashscope的冲突
     *
     * @return
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        DashScopeEmbeddingOptions build = DashScopeEmbeddingOptions.builder().build();
        build.setModel("text-embedding-v2");
        build.setDimensions(1536);
        return new DashScopeEmbeddingModel(new DashScopeApi(apiKey)
                , MetadataMode.EMBED, build);
    }
}