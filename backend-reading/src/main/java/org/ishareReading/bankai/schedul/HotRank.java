package org.ishareReading.bankai.schedul;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.model.HotBook;
import org.ishareReading.bankai.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.ishareReading.bankai.constant.RedisConstant;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @description: 热度排行榜
 * @algorithm: 热度 = (0.3 × 浏览量) + (0.4 × 下载量) + (0.2 × 平均评分 × log(评分人数 + 1)) + (0.1 × exp(-0.05 × 时间差))
 */
@Component
public class HotRank {
    @Autowired
    private BooksService booksService;
    @Autowired
    private RedisTemplate redisTemplate;


    // 书本热度排行榜
    @Scheduled(cron = "0 0 */1 * * ?")        // 每个小时执行一次
    public void bookHotRank(){
        final TopK topK = new TopK(10,new PriorityQueue<HotBook>(10, Comparator.comparing(HotBook::getHot)));
        int limit = 1000;
        Long id = 0L;
        List<Books> books = booksService.list(new LambdaQueryWrapper<Books>()
                .select(Books::getId, Books::getAuthor,Books::getGenre,
                        Books::getCoverImageId,Books::getAverageRating,Books::getRatingCount,Books::getViewCount,
                        Books::getDownloadCount,Books::getUploadTime).gt(Books::getId, id)
                .last("limit " + limit)
        );
        while (books != null && !books.isEmpty()){
            books.parallelStream().forEach( book -> {
                // 计算热度：
                LocalDateTime now = LocalDateTime.now();

                // 计算时间差（单位：天）
                long daysSinceUpload = java.time.Duration.between(book.getUploadTime(), now).toDays();

                // 热度公式
                final double v = weightRandom();
                double heatScore =
                        0.3 * book.getViewCount() +
                                0.4 * book.getDownloadCount() +
                                0.2 * book.getAverageRating().doubleValue() * Math.log(book.getRatingCount() + 1) +
                                0.1 * Math.exp(-0.05 * daysSinceUpload) + v;


               final HotBook hotVideo = new HotBook(heatScore, book.getId(), book.getName(),book.getAuthor(),
                       book.getGenre(),book.getCoverImageId(),book.getViewCount());

                synchronized (topK) {
                    topK.add(hotVideo);
                }
            });
            id = books.get(books.size() - 1).getId();
            books = booksService.list(new LambdaQueryWrapper<Books>()
                    .select(Books::getId, Books::getAuthor,Books::getGenre,
                            Books::getCoverImageId,Books::getAverageRating,Books::getRatingCount,Books::getViewCount,
                            Books::getDownloadCount,Books::getUploadTime).gt(Books::getId, id)
                    .last("limit " + limit)
            );
        }

        Set<ZSetOperations.TypedTuple<HotBook>> tuples = new HashSet<>();
        List<HotBook> hotBooks = topK.get();
        if(ObjectUtils.isEmpty(hotBooks)){
            return;
        }
        Double minHot = hotBooks.get(0).getHot();
        for (HotBook hotBook : hotBooks) {
            ZSetOperations.TypedTuple<HotBook> tuple = new DefaultTypedTuple<>(hotBook,hotBook.getHot());
            tuples.add(tuple);
        }
        redisTemplate.opsForZSet().add(RedisConstant.HOT_BOOK,tuples);
        redisTemplate.opsForZSet().removeRangeByScore(RedisConstant.HOT_BOOK,-1,minHot);

    }

    public double weightRandom() {
        int i = (int) ((Math.random() * 9 + 1) * 100000);
        return i / 1000000.0;
    }
}
