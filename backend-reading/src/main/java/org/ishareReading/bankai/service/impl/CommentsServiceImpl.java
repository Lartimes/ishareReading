package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.model.Comments;
import org.ishareReading.bankai.mapper.CommentsMapper;
import org.ishareReading.bankai.service.CommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

}
