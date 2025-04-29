package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ishareReading.bankai.aop.CommentAop;
import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.mapper.PostsMapper;
import org.ishareReading.bankai.model.Comments;
import org.ishareReading.bankai.model.Posts;
import org.ishareReading.bankai.model.Types;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BooksService;
import org.ishareReading.bankai.service.PostsService;
import org.ishareReading.bankai.service.TypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 帖子表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements PostsService {

    @Autowired
    private TypesService typesService;
    @Autowired
    private CommentAop commentAop;
    @Autowired
    private BooksService booksService;

    @Override
    public List<Posts> getPostsByTypeId(Long id) {
        return this.list(new LambdaQueryWrapper<Posts>().eq(Posts::getTypeId, id)
                .orderByDesc(Posts::getLikeCount, Posts::getViewCount, Posts::getStartCount)
                .last("limit 10"));
    }

    /**
     * record PostsInfo(Posts posts , List<Comments> comments , String directUrl , Types types ) {} 根据postIds获取详情
     */
    @Override
    public PostsInfo getPostInfo(Long id) {
        Posts posts = this.getById(id);
        if (ObjectUtils.isEmpty(posts)) {
            throw new BusinessException("帖子消失不见了......");
        }
        Long typeId = posts.getTypeId();
        Types types = typesService.getById(typeId);
        assert types != null;
        Response response = commentAop.getComment(Map.of("postId", String.valueOf(posts.getId())));
        List<Comments> comments = null;
        if (response.getData() instanceof List) {
            comments = (List<Comments>) response.getData();
        }
        Long bookId = posts.getBookId();
        if (bookId == null) {
            return new PostsInfo(posts, comments, null, types);
        }
        return new PostsInfo(posts, comments, bookId, types);
    }

}
