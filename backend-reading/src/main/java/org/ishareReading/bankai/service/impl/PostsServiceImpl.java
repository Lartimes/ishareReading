package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.model.Posts;
import org.ishareReading.bankai.mapper.PostsMapper;
import org.ishareReading.bankai.service.PostsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
