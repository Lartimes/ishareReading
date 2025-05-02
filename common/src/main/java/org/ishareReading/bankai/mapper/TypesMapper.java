package org.ishareReading.bankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ishareReading.bankai.model.Types;

import java.util.Collection;
import java.util.List;

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
 <![CDATA[
            WITH input_embeddings AS (
                SELECT
                    embedding
                FROM
                    types
                WHERE
                    type = 'books'
                    AND type_name IN
        ]]>
        <foreach item="typeName" collection="typeNames" open="(" separator="," close=")">
            #{typeName}
        </foreach>
        <![CDATA[
            )
            SELECT DISTINCT
                t.*,
                t.embedding <-> ie.embedding AS similarity
            FROM
                types t
            JOIN
                input_embeddings ie ON t.type = 'books'
            ORDER BY
                similarity desc
            limit #{size}
        ]]>
""")
    List<Types> findSimilarTypes(@Param("typeNames") Collection<String> typeNames,
                                 @Param("size") int size);

}
