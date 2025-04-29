package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BaseException;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.mapper.UserFollowingMapper;
import org.ishareReading.bankai.model.UserFollowing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

public interface FollowInterface {

    /**
     * 关注的type 帖子类型、书籍类型 或者 user id 或者 作者名字
     *
     * @param id
     */
    void follow(String id);

    /***
     * 取关
     * @param id
     */
    void unfollow(String id);

}



//帖子类型关注取关ID
@Service
class PostFollowService extends ServiceImpl<UserFollowingMapper,UserFollowing> implements FollowInterface {
    final String type = "PostFollow";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void follow(String id) {
        Long followId = Long.valueOf(id);
        Long userId = UserHolder.get();

        final UserFollowing follow = new UserFollowing();
        follow.setFollowId(id);
        follow.setUserId(userId);
        follow.setType(type);

        try {
            save(follow);
            final Date date = new Date();
            redisTemplate.opsForZSet().add(RedisConstant.POST_FOLLOW + userId, followId, date.getTime());
            redisTemplate.opsForZSet().add(RedisConstant.POST_FANS + followId, userId, date.getTime());
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public void unfollow(String id) {
        Long userId = UserHolder.get();

        try {
            remove(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing::getUserId, userId).eq(UserFollowing::getFollowId,id).eq(UserFollowing::getType,type));
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FOLLOW + userId, id);
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FANS + id, userId);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }
}


//用户关注取关
@Service
class  UserFollowService extends ServiceImpl<UserFollowingMapper,UserFollowing> implements FollowInterface {
    @Autowired
    private RedisTemplate redisTemplate;
    final String type = "UserFollow";
    /**
     *
     * @param id    要关注人的Id
     */
    @Override
    public void follow(String id) {
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
    public void unfollow(String id) {
        Long userId = UserHolder.get();

        try {
             remove(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing::getUserId, userId).eq(UserFollowing::getFollowId,id).eq(UserFollowing::getType,type));
            // 自己关注列表删除
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FOLLOW + id, userId);
            // 对方粉丝列表删除
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FANS + userId, id);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }
}

//书籍类型
@Service
class BookTypeFollowService extends ServiceImpl<UserFollowingMapper,UserFollowing> implements FollowInterface {
    @Autowired
    private RedisTemplate redisTemplate;
    final String type = "BookTypeFollow";

    @Override
    public void follow(String id) {
        Long followId = Long.valueOf(id);
        Long userId = UserHolder.get();

        final UserFollowing follow = new UserFollowing();
        follow.setFollowId(id);
        follow.setUserId(userId);
        follow.setType(type);

        try {
            save(follow);
            final Date date = new Date();
            redisTemplate.opsForZSet().add(RedisConstant.BOOK_TYPE_FOLLOW + userId, followId, date.getTime());
            redisTemplate.opsForZSet().add(RedisConstant.BOOK_TYPE_FANS + followId, userId, date.getTime());
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public void unfollow(String id) {
        Long userId = UserHolder.get();

        try {
            remove(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing::getUserId, userId).eq(UserFollowing::getFollowId,id).eq(UserFollowing::getType,type));
            redisTemplate.opsForZSet().remove(RedisConstant.BOOK_TYPE_FOLLOW + id, userId);
            redisTemplate.opsForZSet().remove(RedisConstant.BOOK_TYPE_FANS + userId, id);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }
}


//作者关注
@Service
class AuthorFollowService extends ServiceImpl<UserFollowingMapper,UserFollowing> implements FollowInterface {

    @Autowired
    private RedisTemplate redisTemplate;
    final String type = "AuthorFollow";


    @Override
    public void follow(String id) {
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
    public void unfollow(String id) {
        Long userId = UserHolder.get();

        try {
            remove(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing::getUserId, userId).eq(UserFollowing::getFollowId,id).eq(UserFollowing::getType,type));
            redisTemplate.opsForZSet().remove(RedisConstant.AUTHOR_FOLLOW + id, userId);
            redisTemplate.opsForZSet().remove(RedisConstant.AUTHOR_FANS + userId, id);
            redisTemplate.opsForZSet().incrementScore(RedisConstant.AUTHOR_FANS_COUNT, id, -1);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }
}

