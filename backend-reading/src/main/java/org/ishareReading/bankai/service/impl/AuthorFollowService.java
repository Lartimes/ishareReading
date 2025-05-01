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

//作者关注
@Service
public  class AuthorFollowService extends ServiceImpl<UserFollowingMapper, UserFollowing> implements FollowInterface {
    public static final String CURRENT_TYPE = "author";
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getType() {
        return CURRENT_TYPE;
    }

    @Override
    public void follow(String id, String type) {
        Long followId = Long.valueOf(id);
        Long userId = UserHolder.get();

        final UserFollowing follow = new UserFollowing();
        follow.setFollowId(id);
        follow.setUserId(userId);
        follow.setType(type);

        try {
            save(follow);
            final Date date = new Date();
            redisTemplate.opsForZSet().add(RedisConstant.AUTHOR_FOLLOW + userId, followId, date.getTime());
            redisTemplate.opsForZSet().add(RedisConstant.AUTHOR_FANS + followId, userId, date.getTime());
            //  同步增加总榜上的粉丝数
            redisTemplate.opsForZSet().incrementScore(RedisConstant.AUTHOR_FANS_COUNT, id, 1);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public void unfollow(String id, String type) {
        Long userId = UserHolder.get();

        try {
            remove(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing::getUserId, userId).eq(UserFollowing::getFollowId, id).eq(UserFollowing::getType, type));
            redisTemplate.opsForZSet().remove(RedisConstant.AUTHOR_FOLLOW + id, userId);
            redisTemplate.opsForZSet().remove(RedisConstant.AUTHOR_FANS + userId, id);
            redisTemplate.opsForZSet().incrementScore(RedisConstant.AUTHOR_FANS_COUNT, id, -1);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }
}
