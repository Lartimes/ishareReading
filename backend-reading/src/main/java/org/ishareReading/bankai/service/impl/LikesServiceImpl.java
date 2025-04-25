package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.model.Likes;
import org.ishareReading.bankai.mapper.LikesMapper;
import org.ishareReading.bankai.service.LikesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 点赞表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes> implements LikesService {

}
