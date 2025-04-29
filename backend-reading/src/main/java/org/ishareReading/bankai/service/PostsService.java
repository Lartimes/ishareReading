package org.ishareReading.bankai.service;

import org.ishareReading.bankai.model.Comments;
import org.ishareReading.bankai.model.Posts;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ishareReading.bankai.model.Types;

import java.util.List;

/**
 * <p>
 * 帖子表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
public interface PostsService extends IService<Posts> {
    /**
     * 获取type属于一类的list posts
     */
    List getPostsByTypeId(Long id);


    /**
     * 帖子实体、评论 、book关联ID(NULL),Types类型名字
     */
    record PostsInfo(Posts posts , List<Comments> comments ,
                     Long bookId , Types types ) {}
    PostsInfo getPostInfo(Long id);
}
