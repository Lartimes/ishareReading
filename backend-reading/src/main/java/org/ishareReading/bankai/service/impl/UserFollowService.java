package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BaseException;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.mapper.UserFollowingMapper;
import org.ishareReading.bankai.model.UserFollowing;
import org.ishareReading.bankai.service.FollowInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

//用户关注取关
@Service
public  class UserFollowService extends ServiceImpl<UserFollowingMapper, UserFollowing> implements FollowInterface {
    public static final String CURRENT_TYPE = "users";
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getType() {
        return CURRENT_TYPE;
    }

    /**
     * @param id 要关注人的Id
     */
    @Override
    public void follow(String id, String type) {
        Long followId = Long.valueOf(id);
        Long userId = UserHolder.get();
        if (userId.equals(followId)) {
            throw new BaseException("你不能关注自己");
        }

        final UserFollowing follow = new UserFollowing();
        follow.setFollowId(id);
        follow.setUserId(userId);
        follow.setType(type);

        try {
            save(follow);
            final Date date = new Date();
            // 自己关注列表添加
            redisTemplate.opsForZSet().add(RedisConstant.USER_FOLLOW + userId, followId, date.getTime());
            // 对方粉丝列表添加
            redisTemplate.opsForZSet().add(RedisConstant.USER_FANS + followId, userId, date.getTime());
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }

    }

    @Override
    public void unfollow(String id, String type) {
        Long userId = UserHolder.get();

        try {
            remove(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing::getUserId, userId).eq(UserFollowing::getFollowId, id).eq(UserFollowing::getType, type));
            // 自己关注列表删除
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FOLLOW + id, userId);
            // 对方粉丝列表删除
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FANS + userId, id);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }
}
