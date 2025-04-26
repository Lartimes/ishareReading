package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.mapper.LikesMapper;
import org.ishareReading.bankai.model.Likes;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.response.ResponseCode;
import org.ishareReading.bankai.service.LikesService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.ishareReading.bankai.constant.Constant.LIKE_ITEMS;

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
    private static final Logger LOG = LogManager.getLogger(LikesServiceImpl.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /**
     * 采用什么数据结构？？ hashmap : 存储谁喜欢了
     * 但是hashmap内存占比度相比zset还是太高了
     *
     * @param objectId
     * @param type
     *
     * @return
     */
    @Override
    public Response likeObject(Long objectId, String type, Long userId) {
        if (!LIKE_ITEMS.contains(type)) {
            throw new BusinessException(ResponseCode.FAILURE);
        }
        Map<Object, Object> map = Map.of(userId, LocalDateTime.now());
        String key = String.join(":", RedisConstant.LIKE_PREFIX, type, String.valueOf(objectId));
        boolean hmset = redisCacheUtil.hmset(key, map);
        key = RedisConstant.LIKE_LISTENER + type;
        Boolean listener = redisCacheUtil.addZSetWithScores(key, objectId, null);
        if (hmset && listener) {
//            点赞总数先不做处理 ，定时任务进行同步redis再写入
            return Response.success("点赞成功");
        }
        throw new BusinessException(ResponseCode.SERVER_ERROR);
    }

    @Override
    public Response unlikeObject(Long objectId, String type, Long userId) {
        if (!LIKE_ITEMS.contains(type)) {
            throw new BusinessException(ResponseCode.FAILURE);
        }
        String key = String.join(":", RedisConstant.LIKE_PREFIX, type, String.valueOf(objectId));
        boolean hmset = redisCacheUtil.hRemove(key, Collections.singleton(userId));
        if (hmset) {
            return Response.success("取消点赞");
        }
        throw new BusinessException(ResponseCode.SERVER_ERROR);
    }


    /**
     * 获取关注的所有bean表
     * 定时任务
     * 异步处理
     *
     * @param type
     * @param objectId
     * @return
     */
    @Async
    @Override
    public List<Likes> getLikedCountFromRedis(String type, Long objectId) {
        if (!redisCacheUtil.isMember(RedisConstant.LIKE_LISTENER + type, objectId)) {
            //如果触发过点赞的话进行统计
            return Collections.emptyList();
        }
        ArrayList<Likes> arr = new ArrayList<>();
        RedisTemplate<String, Object> redisTemplate = redisCacheUtil.getRedisTemplate();
        String pk = String.join(":", RedisConstant.LIKE_PREFIX, type, String.valueOf(objectId));
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(pk, ScanOptions.NONE);
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> map = cursor.next();
            String userId = (String) map.getKey();
            Likes likes = new Likes();
            likes.setObjectId(objectId);
            likes.setType(type);
            likes.setType(userId);
            arr.add(likes);
        }
        cursor.close();
        redisCacheUtil.removeZSetValue(RedisConstant.LIKE_LISTENER + type, objectId);
        //删除点赞动作的目标对象
        return arr;
    }


}
