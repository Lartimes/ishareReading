package org.ishareReading.bankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
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
    List<Types> findSimilarTypes(@Param("typeName") Collection<String> typeNames,
                                 @Param("size") int size);

}
