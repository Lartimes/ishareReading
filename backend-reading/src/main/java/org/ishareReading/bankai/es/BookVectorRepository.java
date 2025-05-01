package org.ishareReading.bankai.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookVectorRepository extends ElasticsearchRepository<BookVector, String> {
}
