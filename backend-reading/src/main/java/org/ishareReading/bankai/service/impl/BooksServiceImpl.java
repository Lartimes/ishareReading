package org.ishareReading.bankai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.mapper.BooksMapper;
import org.ishareReading.bankai.model.HotBook;
import org.ishareReading.bankai.service.BooksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 书籍表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {
    @Autowired
    RedisTemplate redisTemplate;

    final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public List<HotBook> getBookHotRank() {
        final Set<ZSetOperations.TypedTuple<Object>> zSet = redisTemplate.opsForZSet().reverseRangeWithScores(RedisConstant.HOT_BOOK, 0, -1);
        final ArrayList<HotBook> hotBooks = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> objectTypedTuple : zSet) {
            final HotBook hotBook;
            try {
                hotBook = objectMapper.readValue(objectTypedTuple.getValue().toString(), HotBook.class);
                hotBook.setHot((double) objectTypedTuple.getScore().intValue());
                hotBook.hotFormat();
                hotBooks.add(hotBook);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return hotBooks;
    }
}
