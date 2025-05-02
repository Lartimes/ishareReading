package org.ishareReading.bankai.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "books_vector", createIndex = true)
public class BookVector {
    @Id
    private String id;
    @Field(type = FieldType.Keyword, index = true)
    private String name;

    private Set<Long> ids;
    @Field(type = FieldType.Dense_Vector, dims = 300,
            index = true, similarity = "cosine", name = "embedding") //与NLP训练模型一致
    private float[] embedding; // 用于 KNN 搜索的向量

    public BookVector(String name, Set<Long> ids, float[] embedding) {
        this.name = name;
        this.ids = ids;
        this.embedding = embedding;
    }

    public List<Float> buildVector() {
        List<Float> result = new ArrayList<Float>();
        for (float v : embedding) {
            result.add(v);
        }
        return result;
    }

//    public void setEmbedding(float[] embedding) {
//        if (embedding == null) {
//            this.embedding = null;
//            return;
//        }
//        this.embedding = new double[embedding.length];
//        for (int i = 0; i < embedding.length; i++) {
//            this.embedding[i] = embedding[i];
//        }
//    }
}