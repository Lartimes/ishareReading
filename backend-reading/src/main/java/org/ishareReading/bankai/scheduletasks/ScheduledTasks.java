package org.ishareReading.bankai.scheduletasks;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.ishareReading.bankai.constant.Constant;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.exception.BaseException;
import org.ishareReading.bankai.mapper.*;
import org.ishareReading.bankai.model.*;
import org.ishareReading.bankai.service.LikesService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    private static final long PAGE_SIZE = 100L;
    private static final int BATCH_SIZE = 1000;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final RedisCacheUtil redisCacheUtil;
    private final LikesService likesService;
    private final BooksMapper booksMapper;
    private final BookOpinionsMapper bookOpinionsMapper;
    private final CommentsMapper commentsMapper;
    private final PostsMapper postsMapper;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Autowired
    @Qualifier("batchSqlSessionFactory")
    private SqlSessionFactory batchSqlSessionFactory;

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

    /**
     * 第二种方案
     * bitmap + zset + 并交集 + 长连接 + 差异更新 + 回写cache
     */
//    @Scheduled(cron = "0 0/30 * * * ?") // 每 30 分钟的第 0 秒执行一次
    public void updateBooksStar() {
        try (SqlSession sqlSession = batchSqlSessionFactory.openSession()) {
//                list foreach ---> 执行？
            long now = System.currentTimeMillis();
            LOG.info("定时任务updateBooksStar(): 获取长连接connection,以及batch sqlsession");
            updateBooksStar(sqlSession);
            long then = System.currentTimeMillis();
            LOG.info("定时任务updateBooksStar(): 完成,耗时:{}秒", (then - now) / 1e3);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new BaseException(e.getMessage());
        }
    }

    //    TODO : 表的分片设计 提高效率 此处直接查询，到redis推上去就行集合的运算
    private void updateBooksStar(SqlSession sqlSession) {
        Set<Long> booksIds = booksMapper.selectList(new LambdaQueryWrapper<Books>()
                .select(Books::getId)).stream().map(Books::getId).collect(Collectors.toSet());
        LOG.info("updateBooksStar(SqlSession sqlSession): 逐步写入redis db likeIDs");
        for (Long bookId : booksIds) {
//            分页返回，写入zset
            Long count = likesService.count(new LambdaQueryWrapper<Likes>()
                    .eq(Likes::getObjectId, bookId)
                    .eq(Likes::getType, "books"));
            if (count != 0L) {
                long loops = 1L;
                long tmp = count / PAGE_SIZE;
                if (tmp > 0) {
                    loops += tmp;
                }
                for (int i = 0; i < loops; i++) {
                    Set<Object> userIds = likesService.page(new Page<Likes>(i, PAGE_SIZE),
                            new LambdaQueryWrapper<Likes>()
                                    .eq(Likes::getObjectId, bookId)
                                    .select(Likes::getUserId)).getRecords().stream().map(Likes::getUserId).collect(Collectors.toSet());
                    String key = String.join(":", RedisConstant.LIKE_PREFIX, "books", String.valueOf(bookId));
                    redisCacheUtil.addMembers("DB:" + key, userIds);
                }
            }
        }
        LOG.info("进行集合运算，进行更新DB :  db ∩ redis");
        try {
            LikesMapper mapper = sqlSession.getMapper(LikesMapper.class);
//            两个集合进行筛选出独特的 一个remove ， 一个update
            for (Long bookId : booksIds) {
                String tmp = "diff:" + bookId;
                String key = String.join(":", RedisConstant.LIKE_PREFIX, "books", String.valueOf(bookId));
                Set<Long> removedIds = redisCacheUtil.differenceIntersectionAlternative("DB:" + key,
                        key, tmp).stream().map((id -> Long.parseLong(id.toString()))).collect(Collectors.toSet());
                LOG.info("removedIds : {}", removedIds);
                List<Likes> addedStars = redisCacheUtil.differenceIntersectionAlternative(key,
                                "DB:" + key, tmp).stream()
                        .map(((id -> {
                            Likes likes = new Likes();
                            likes.setId(null);
                            likes.setCreateAt(LocalDateTime.now());
                            likes.setUserId(Long.parseLong(id.toString()));
                            likes.setObjectId(bookId);
                            return likes;
                        }))).toList();
                LOG.info("addedStars : {}", addedStars);
                //                db - db ∩ redis
                //                redis - db ∩ redis
                boolean isUnliked = !removedIds.isEmpty();
                if (isUnliked) {
                    mapper.delete(new LambdaQueryWrapper<Likes>()
                            .eq(Likes::getObjectId, bookId)
                            .in(Likes::getUserId, removedIds));
                }
                LOG.info("bookId 三十分钟内{}取消点赞:{}", isUnliked ? "有" : "无", removedIds.size());
                int size = addedStars.size();
                if (size == 0) {
                    LOG.info("bookId 三十分钟内无点赞:{}", bookId);
                    continue;
                }
                int count = 0;
                for (int i = 0; i < size; i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, size);
                    count += mapper.insertBatchSomeColumn(addedStars.subList(i, end));
                }
                if (count == size) {
                    LOG.info("异步写入DB成功---bookId:{}", bookId);
                }
            }
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback(true);
            LOG.error(e.getMessage());
            throw new BaseException(e.getMessage());
        }
    }
}