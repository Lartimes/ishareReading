package org.ishareReading.bankai.scheduletasks;

import org.ishareReading.bankai.constant.Constant;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.mapper.BookOpinionsMapper;
import org.ishareReading.bankai.mapper.BooksMapper;
import org.ishareReading.bankai.mapper.CommentsMapper;
import org.ishareReading.bankai.mapper.PostsMapper;
import org.ishareReading.bankai.model.*;
import org.ishareReading.bankai.service.LikesService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ScheduledTasks {

    private final ThreadPoolTaskScheduler taskScheduler;

    private final RedisCacheUtil redisCacheUtil;

    private final LikesService likesService;
    private final BooksMapper booksMapper;
    private final BookOpinionsMapper bookOpinionsMapper;
    private final CommentsMapper commentsMapper;
    private final PostsMapper postsMapper;

    public ScheduledTasks(RedisCacheUtil redisCacheUtil, @Qualifier(value = "customThreadPoolTaskScheduler") ThreadPoolTaskScheduler taskScheduler, LikesService likesService, BooksMapper booksMapper, BookOpinionsMapper bookOpinionsMapper, CommentsMapper commentsMapper, PostsMapper postsMapper) {
        this.redisCacheUtil = redisCacheUtil;
        this.taskScheduler = taskScheduler;
        this.likesService = likesService;
        this.booksMapper = booksMapper;
        this.bookOpinionsMapper = bookOpinionsMapper;
        this.commentsMapper = commentsMapper;
        this.postsMapper = postsMapper;
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void performTask() {
//        getLikedCountFromRedis
//        并行fork 抢占式处理点赞量
//        行锁进行数据库 + redis 延时一致
//                       likesService.saveBatch(likedCountFromRedis);
//        int addedCount = likedCountFromRedis.size();
        Constant.LIKE_ITEMS.stream().parallel().forEach(likeType -> {
            Collection<Object> objects = redisCacheUtil.getZSetByKey(RedisConstant.LIKE_LISTENER + likeType);
            taskScheduler.execute(() -> {
                objects.stream().map(a -> Long.parseLong(a.toString())).parallel().forEach(
                        id -> {
                            try {
                                List<Likes> likedCountFromRedis = likesService.getLikedCountFromRedis(likeType, id);
                                likesService.saveBatch(likedCountFromRedis);
                                int added = likedCountFromRedis.size();
                                refreshDb(added, id, likeType);
                            } catch (Exception ignore) {
                                System.out.println("定位任务更新点赞出现错误");
                            }
                        });
            });
        });
    }

    /**
     * 根据like type + objId 行锁更新数据库 + 回写redis update a set x = x + 1 where .... 此处不写了,mp不支持, 必须手动写sql 可以写一个commonmapper
     * 但此为快速开发,不写了
     *
     * @param added
     * @param id
     * @param likeType
     */
    private void refreshDb(int added, Long id, String likeType) {
        String key = String.join(":", RedisConstant.LIKE_COUNT, likeType, id.toString());
        if (likeType.equalsIgnoreCase(Constant.LIKE.COMMENT.toString())) {
            Comments comments = commentsMapper.selectById(id);
            comments.setLikeCount(comments.getLikeCount() + added);
            commentsMapper.updateById(comments);
            redisCacheUtil.set(key, comments.getLikeCount());
        } else if (likeType.equalsIgnoreCase(Constant.LIKE.POST.toString())) {
            Posts posts = postsMapper.selectById(id);
            posts.setLikeCount(posts.getLikeCount() + added);
            postsMapper.updateById(posts);
            redisCacheUtil.set(key, posts.getLikeCount());
        } else if (likeType.equalsIgnoreCase(Constant.LIKE.BOOK.toString())) {
            Books books = booksMapper.selectById(id);
            books.setRatingCount(books.getRatingCount() + added); //没有这个字段啊,不改了rating当作点赞数
            booksMapper.updateById(books);
            redisCacheUtil.set(key, books.getRatingCount());
        } else if (likeType.equalsIgnoreCase(Constant.LIKE.OPINION.toString())) {
            BookOpinions bookOpinions = bookOpinionsMapper.selectById(id);
            bookOpinions.setLikeCount(bookOpinions.getLikeCount() + added);
            bookOpinionsMapper.updateById(bookOpinions);
            redisCacheUtil.set(key, bookOpinions.getLikeCount());
        }
    }
}