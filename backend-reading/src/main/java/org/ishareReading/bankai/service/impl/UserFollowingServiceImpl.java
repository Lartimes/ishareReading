package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.model.UserFollowing;
import org.ishareReading.bankai.mapper.UserFollowingMapper;
import org.ishareReading.bankai.service.UserFollowingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户关注表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class UserFollowingServiceImpl extends ServiceImpl<UserFollowingMapper, UserFollowing> implements UserFollowingService {

}
