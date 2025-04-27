package org.ishareReading.bankai.schedul;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.model.HotBook;
import org.ishareReading.bankai.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.ishareReading.bankai.constant.RedisConstant;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @descripetion:热度排行榜
 * @algorithm: 热度 = (0.3 × 浏览量) + (0.4 × 下载量) + (0.2 × 平均评分 × log(评分人数 + 1)) + (0.1 × exp(-0.05 × 时间差))
 */
@Component
public class HotRank {
    private static final Logger LOG = LogManager.getLogger(HotRank.class);

    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    ObjectMapper om = new ObjectMapper();

    @Autowired
    private BooksService booksService;
    @Autowired
    private RedisTemplate redisTemplate;


    // 书本热度排行榜
    @Scheduled(cron = "0 0 */1 * * ?")        // 每隔小时执行一次
    public void bookHotRank(){
        LOG.info("book hot rank");
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


        final byte[] key = RedisConstant.HOT_BOOK.getBytes();
        final List<HotBook> hotBooks = topK.get();
        if(ObjectUtils.isEmpty(hotBooks)){
            return;
        }
        Double minHot = hotBooks.get(0).getHot();

        redisTemplate.opsForZSet().removeRange(RedisConstant.HOT_BOOK, 0,-1);

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (HotBook hotBook : hotBooks) {
                final Double hot = hotBook.getHot();
                try {
                    connection.zAdd(key, hot, jackson2JsonRedisSerializer.serialize(om.writeValueAsString(hotBook)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

    }






    public double weightRandom() {
        int i = (int) ((Math.random() * 9 + 1) * 100000);
        return i / 1000000.0;
    }


    // 热门书
//    @Scheduled(cron = "0 0 */3 * * ?")
//    public void hotBook() {
//        // 分片查询3天内的视频
//        int limit = 1000;
//        long id = 1;
//        List<Book> books = booksService.selectNDaysAgeVideo(id, 3, limit);
//        //final Double hotLimit = settingService.list(new LambdaQueryWrapper<Setting>()).get(0).getHotLimit();
//        final  Double hotLimit = 100.0;
//        Calendar calendar = Calendar.getInstance();
//        int today = calendar.get(Calendar.DATE);
//
//        while (!ObjectUtils.isEmpty(books)) {
//            final ArrayList<Long> hotVideos = new ArrayList<>();
//            for (Book book : books) {
//
//                // 大于X热度说明是热门视频
//                if (hot > hotLimit) {
//                    hotVideos.add(book.getId());
//                }
//
//            }
//            id = books.get(books.size() - 1).getId();
//            videos = videoService.selectNDaysAgeVideo(id, 3, limit);
//            // RedisConstant.HOT_VIDEO + 今日日期 作为key  达到元素过期效果
//            if (!ObjectUtils.isEmpty(hotVideos)){
//                String key = RedisConstant.HOT_VIDEO + today;
//                redisTemplate.opsForSet().add(key, hotVideos.toArray(new Object[hotVideos.size()]));
//                redisTemplate.expire(key, 3, TimeUnit.DAYS);
//            }
//
//        }
//    }


    // TODO
    // 热门作者
    @Scheduled(cron = "0 0 */3 * * ?")
    public void hotAuthor(){
        Set<String> keys = redisTemplate.keys(RedisConstant.AUTHOR_FANS);
        for (String key : keys) {
            // 获取该用户关注的所有作者
            Long count = redisTemplate.opsForZSet().count(key, 0, -1);

        }
    }


}
