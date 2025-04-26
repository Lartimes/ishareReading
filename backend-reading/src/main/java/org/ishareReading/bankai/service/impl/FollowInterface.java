package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BaseException;
import org.ishareReading.bankai.mapper.UserFollowingMapper;
import org.ishareReading.bankai.model.UserFollowing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

//TODO
public interface FollowInterface {

    /**
     * 关注的type 帖子类型、书籍类型 或者 user id 或者 作者名字
     *
     * @param id
     */
    void follow(String type, String id);

    /***
     * 取关
     * @param type
     * @param id
     */
    void unfollow(String type, String id);

}

//帖子类型关注取关ID
@Service
class PostFollowService implements FollowInterface {
    @Override
    public void follow(String type, String id) {

    }

    @Override
    public void unfollow(String type, String id) {

    }
}


//用户关注取关
@Service
class  UserFollowService extends ServiceImpl<UserFollowingMapper,UserFollowing> implements FollowInterface {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     *
     * @param type  要关注人的Id
     * @param id    用户的Id
     */
    @Override
    public void follow(String type, String id) {
        if (type.equals(id)) {
            throw new BaseException("你不能关注自己");
        }

        // 直接保存(唯一索引),保存失败则删除
        final UserFollowing follow = new UserFollowing();
        follow.setFollowId(Long.valueOf(type));
        follow.setUserId(Long.valueOf((id)));
        try {
            save(follow);
            final Date date = new Date();
            // 自己关注列表添加
            redisTemplate.opsForZSet().add(RedisConstant.USER_FOLLOW + id, type, date.getTime());
            // 对方粉丝列表添加
            redisTemplate.opsForZSet().add(RedisConstant.USER_FANS + type, id, date.getTime());
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }

    }

    @Override
    public void unfollow(String type, String id) {
        // 直接保存(唯一索引),保存失败则删除
        final UserFollowing follow = new UserFollowing();
        follow.setFollowId(Long.valueOf(type));
        follow.setUserId(Long.valueOf((id)));
        try {
             this.remove(new LambdaQueryWrapper<UserFollowing>().eq(UserFollowing::getUserId, id).eq(UserFollowing::getFollowId,type));
            // 自己关注列表添加
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FOLLOW + id, type);
            // 对方粉丝列表添加
            redisTemplate.opsForZSet().remove(RedisConstant.USER_FANS + type, id);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }
}

//书籍类型
@Service
class BookTypeFollowService implements FollowInterface {
    @Override
    public void follow(String type, String id) {

    }

    @Override
    public void unfollow(String type, String id) {

    }
}


//作者关注
@Service
class AuthorFollowService implements FollowInterface {
    @Override
    public void follow(String type, String id) {

    }

    @Override
    public void unfollow(String type, String id) {

    }
}

