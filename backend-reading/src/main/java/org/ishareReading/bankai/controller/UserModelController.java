package org.ishareReading.bankai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.constant.UserModelConstant;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.mapper.TypesMapper;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.model.Posts;
import org.ishareReading.bankai.model.Types;
import org.ishareReading.bankai.model.UserModel;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BooksService;
import org.ishareReading.bankai.service.PostsService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户模型相关的接口 阅读如何评分？ 定时发送，阅读中.... 3 ， 这个阅读时长应该搞一下，搞成指数或者非线性，这个很相关 点赞书籍  1 点评书籍  4 标注书籍   1 点赞书籍相关任意内容 1 收藏书籍： 5
 * <p>
 * 帖子如何评分？ 收藏帖子： 5 点赞帖子： 1 评论帖子： 3 阅读帖子 0.5
 */
@RestController
@RequestMapping("/userModel")
public class UserModelController {
    private static final Integer PUSH_SIZE = 15;
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BooksService booksService;


    //   TODO 推送帖子和书籍和其他用户
    @Autowired
    private TypesMapper typesMapper;
    private PostsService postsService;

    /**
     * 默认一次推送types，用户模型的这些 ， 一次推送15条
     */
    @SneakyThrows
    @GetMapping("/pushBooks")
    public Response pushBooksByModel() {
        Long userId = UserHolder.get();
        if (userId == null) {
//            直接随同随机type books
            List<String> keysByPrefix = redisCacheUtil.getKeysByPrefix(RedisConstant.BOOK_TYPE);
            ArrayList<Long> longs = new ArrayList<>();
            List<Long> pipelines = redisCacheUtil.pipeline((connection -> {
                keysByPrefix.forEach(key -> {
                    connection.zSetCommands().zRandMember(key.getBytes(StandardCharsets.UTF_8));
                });
                return null;
            }));
            List<Books> books = booksService.listByIds(pipelines);
            List<BooksService.BooksInfoHomePage> booksInfoHomePages = booksService.convert2HomePage(books);
            int pushSize = booksInfoHomePages.size();
            booksInfoHomePages = pushSize > PUSH_SIZE ? booksInfoHomePages.subList(PUSH_SIZE, pushSize) : booksInfoHomePages;
            return Response.success(booksInfoHomePages);
        }
        var objectCollection = redisCacheUtil.getZSetByKey(RedisConstant.BOOKS_HISTORY + userId);
        Set<Long> bookIds = objectCollection.stream()
                .filter(Objects::nonNull)
                .map(obj -> Long.parseLong((String) obj))
                .collect(Collectors.toSet());
        String value = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
        UserModel userModel = objectMapper.readValue(value, UserModel.class);
        String[] strings = userModel.initBooksProbabilityArray();
        Random random = new Random();
        Collection<String> types = Arrays.stream(strings).collect(Collectors.toSet());
        int size = types.size();

        List<Types> similarTypes = typesMapper.findSimilarTypes(types, size + size >> 1);
        Set<String> collect = similarTypes.stream().map(Types::getTypeName).collect(Collectors.toSet());
        collect.removeAll(types); //删除概率数组中的，type库中随机拿
        ArrayList<String> pushTypes = new ArrayList<>();
        size = Math.max(size, PUSH_SIZE);
        for (int i = 0; i < size; i++) {
            int index = random.nextInt(0, strings.length);
            pushTypes.add(strings[index]);
        }
        pushTypes.addAll(collect);
        List<String> list = pushTypes.stream().map(a -> RedisConstant.BOOK_TYPE + a).toList();
        //即将推荐的书籍IDS
        Set<Long> ids = redisCacheUtil.sRandom(list).stream()
                .map(id -> Long.parseLong(id.toString()))
                .filter(a -> !bookIds.contains(a))
                .collect(Collectors.toSet());
//        推荐的也要加到history...
        byte[] key = (RedisConstant.BOOKS_HISTORY + userId).getBytes(StandardCharsets.UTF_8);
        redisCacheUtil.pipeline((connection) -> {
            ids.forEach(id -> {
                connection.zSetCommands().zAdd(key, System.currentTimeMillis(), id.toString().getBytes(StandardCharsets.UTF_8));
            });
            return null;
        });

        List<Books> books = booksService.listByIds(ids);
        List<BooksService.BooksInfoHomePage> booksInfoHomePages = booksService.convert2HomePage(books);
        int pushSize = booksInfoHomePages.size();
        booksInfoHomePages = pushSize > PUSH_SIZE ? booksInfoHomePages.subList(PUSH_SIZE, pushSize) : booksInfoHomePages;
        return Response.success(booksInfoHomePages);
    }

    /**
     * 随便推荐吧，跟书籍同理
     */
    @SneakyThrows
    @GetMapping("/pushPostsByModel")
    public Response pushPosts() {
        Long userId = UserHolder.get();
        if (userId == null) {
            List<Posts> list = postsService.list();
            Collections.shuffle(list);
            list.subList(0, Math.min(PUSH_SIZE, list.size()));
            return Response.success(list);
        }
        String value = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
        UserModel userModel = objectMapper.readValue(value, UserModel.class);
        Long[] longs = userModel.initPostsProbabilityArray(); //typeId
        Random random = new Random();


        return null;
    }

    /**
     * 阅读时更新用户模型 登陆的时候就要初始化模型了 阅读界面，前端定时ping
     */
    @GetMapping("/ping")
    @SneakyThrows
    public void ping(@RequestParam Long bookId) {
        Long userId = UserHolder.get();
        String userModelStr = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
        UserModel userModel = objectMapper.readValue(userModelStr, UserModel.class);
        userModel.setReadingTime(userModel.getReadingTime() + 5);
        Set<UserModel.TypeScore<Books>> bookTypes = userModel.getBookTypes();
        boolean flag = false;
        for (UserModel.TypeScore<Books> bookType : bookTypes) {
            if (bookType.t.getId().equals(bookId)) {
                bookType.time += 5;
                bookType.score = bookType.score + UserModelConstant.READING.PING;
                flag = true;
            }
        }
        if (!flag) {
            UserModel.TypeScore<Books> booksTypeScore = new UserModel.TypeScore<>();
            booksTypeScore.t = booksService.getById(bookId);
            booksTypeScore.time = 5;
            booksTypeScore.score = 5.0;
            booksTypeScore.readDate = LocalDate.now();
            userModel.getBookTypes().add(booksTypeScore);
        }
        redisCacheUtil.set(RedisConstant.USER_MODEL + userId, objectMapper.writeValueAsString(userModel));
    }


}
