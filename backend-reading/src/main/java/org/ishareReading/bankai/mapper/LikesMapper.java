package org.ishareReading.bankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ishareReading.bankai.model.Likes;

import java.util.Collection;

/**
 * <p>
 * 点赞表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:36:41
 */
public interface LikesMapper extends BaseMapper<Likes> {

        Integer insertBatchSomeColumn(Collection<Likes> entityList);
}
