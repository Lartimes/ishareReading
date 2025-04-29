package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.model.AuthorFanInfo;
import org.ishareReading.bankai.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// TODO 建议添加一个author表 就 id 、 name 和  head_portrait 字段
@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    RedisTemplate redisTemplate;
    public List<AuthorFanInfo> getAuthorHotRank() {
        Set<ZSetOperations.TypedTuple<String>> topAuthors =
                redisTemplate.opsForZSet().reverseRangeWithScores(RedisConstant.AUTHOR_FANS_COUNT, 0, 4);

        List<AuthorFanInfo> result = new ArrayList<>();
        if (topAuthors != null) {
            for (ZSetOperations.TypedTuple<String> author : topAuthors) {
                String authorId = author.getValue();
                Double fans = author.getScore(); // 粉丝数是 Double 类型
                if (authorId != null && fans != null) {
                    result.add(new AuthorFanInfo(Long.parseLong(authorId), fans.intValue()));
                }
            }
        }
        return result;
    }
}
