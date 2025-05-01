package org.ishareReading.bankai.es;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookVectorService {

    private static final String INDEX_NAME = "books_vector";
    private final ElasticsearchClient elasticsearchClient;
    private final BookVectorRepository bookVectorRepository;


    public BookVectorService(ElasticsearchClient elasticsearchClient, BookVectorRepository bookVectorRepository) {
        this.elasticsearchClient = elasticsearchClient;
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
     *
     */
    public List<BookVector> knnSearch(Collection<BookVector> vectorList) {
//        入参改成keywords， 《string》
        ArrayList<BookVector> result = new ArrayList<>();
        for (BookVector wordVector : vectorList) {
            KnnQuery knn = new KnnQuery.Builder().field("embedding")
                    .queryVector(wordVector.buildVector())
                    .k(20).numCandidates(100)
                    .build();
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .knn(knn)
            );
            try {
                SearchResponse<Void> searchResponse = elasticsearchClient.search(searchRequest, Void.class);
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
