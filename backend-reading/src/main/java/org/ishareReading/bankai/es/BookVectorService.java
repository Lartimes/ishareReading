package org.ishareReading.bankai.es;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookVectorService {

    private static final String INDEX_NAME = "books_vector";
    private final ElasticsearchClient esClient;
    private final BookVectorRepository bookVectorRepository;

    public BookVectorService(@Qualifier("esClient") ElasticsearchClient esClient, BookVectorRepository bookVectorRepository) {
        this.esClient = esClient;
        this.bookVectorRepository = bookVectorRepository;
    }


    /**
     * 批量添加向量
     *
     * @param vectorList
     */
    public void saveVector(List<BookVector> vectorList) {
        if (ObjectUtils.isEmpty(vectorList)) {
            return;
        }
        vectorList = vectorList.stream().filter(Objects::nonNull).toList();
        if (vectorList.isEmpty()) {
            return;
        }

        bookVectorRepository.saveAll(vectorList);
    }

    /**
     * 获取所有
     */
    public List<BookVector> getAllVectors() {
        Iterable<BookVector> all = bookVectorRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * KNN查询
     */
    public List<BookVector> knnSearch(Collection<BookVector> vectorList) {
        ArrayList<BookVector> result = new ArrayList<>();
        for (BookVector bookVector : vectorList) {
            KnnSearch knnSearch = KnnSearch.of(ks -> ks
                    .field("embedding")
                    .queryVector(bookVector.buildVector())
                    .k(10)
                    .numCandidates(100)
                    .filter(Collections.emptyList()) // 这里示例设置为空列表，你可以根据需要添加过滤器
            );
            SearchRequest searchRequest = SearchRequest.of(sr -> sr
                    .index(INDEX_NAME)
                    .knn(Collections.singletonList(knnSearch))
            );
            try {
                SearchResponse<Void> searchResponse = esClient.search(searchRequest, Void.class);
                List<Hit<Void>> hits = searchResponse.hits().hits();
                Set<String> set = hits.stream().map(Hit::id).collect(Collectors.toSet());
                Iterable<BookVector> wordVectors = bookVectorRepository.findAllById(set);
                result.addAll((Collection<? extends BookVector>) wordVectors);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

}
