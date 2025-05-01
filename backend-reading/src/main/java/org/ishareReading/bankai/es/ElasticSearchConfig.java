package org.ishareReading.bankai.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/8 16:13
 */
@Configuration
public class ElasticSearchConfig {
    @Value("${spring.data.elasticsearch.uri}")
    private String uri;

    @Bean
    public ElasticsearchClient esClient() {
        RestClient restClient = RestClient
                .builder(HttpHost.create(uri))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }


}
