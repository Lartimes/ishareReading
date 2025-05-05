package org.ishareReading.bankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.ishareReading.bankai.model.Types;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * type Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:36:41
 */
public interface TypesMapper extends BaseMapper<Types> {

    /**
     * pgvector knn搜索相似的types
     */
    @Select("""
                SELECT DISTINCT
                    t.*,
                    t.embedding <-> ie.embedding AS similarity
                FROM
                    types t
                JOIN (
                    SELECT
                        embedding
                    FROM
                        types
                    WHERE
                        type = 'books'
                        AND type_name IN (${typeNames})
                ) ie ON t.type = 'books'
                ORDER BY
                    similarity DESC
                LIMIT #{size}
            """)
    List<Types> findSimilarTypes(Map<String, Object> map);

}
