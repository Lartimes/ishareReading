package org.ishareReading.bankai.service;

import org.ishareReading.bankai.model.Likes;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ishareReading.bankai.response.Response;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * <p>
 * 点赞表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
public interface LikesService extends IService<Likes> {

    /**
     *点赞事物：
     *   帖子 ，评论， 书籍，见解
     *   可以是这些
     */
    Response likeObject(Long objectId, String type, Long userId);

    Response unlikeObject(Long objectId, String type, Long userId);

    @Async
    List<Likes> getLikedCountFromRedis(String type, Long objectId);
}
