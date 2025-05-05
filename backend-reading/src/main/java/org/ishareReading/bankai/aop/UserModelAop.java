package org.ishareReading.bankai.aop;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.constant.UserModelConstant;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.BookUnderlineCoordinates;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.model.Posts;
import org.ishareReading.bankai.model.UserModel;
import org.ishareReading.bankai.service.BooksService;
import org.ishareReading.bankai.service.PostsService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * 用户模型AOP
 */
@Aspect
@Component
public class UserModelAop {
    /**
     * 点赞？？ org.ishareReading.bankai.service.BooksService#getBooksInfoById(java.lang.Long) 浏览书籍
     * org.ishareReading.bankai.service.BooksService#getBooksInfoReadingModeById(java.lang.Long, java.lang.Integer,
     * java.lang.Long) 阅读书籍 org.ishareReading.bankai.controller.UserModelController#ping(java.lang.Long) ---> 这个搞成接口来测
     * org.ishareReading.bankai.service.BooksService#markBook(org.ishareReading.bankai.model.BookUnderlineCoordinates)发表见解
     * org.ishareReading.bankai.controller.FavoriteController#doOrUndoFavorite(java.lang.String, java.lang.Long)
     * 收藏书籍、或者帖子
     * <p>
     * org.ishareReading.bankai.aop.FollowOrSubscribeAop#likeOrUnlikeObject(java.util.Map) ---》 关注 String type =
     * map.get("type"); String id = map.get("id"); * 收藏或者取消收藏 * books * posts
     * org.ishareReading.bankai.aop.CommentAop#comment(java.util.Map) 评论，帖子，或者是 书籍首页
     */
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BooksService booksService;
    @Autowired
    private PostsService postsService;

    @SneakyThrows
    @Around("execution(* org.ishareReading.bankai.service.impl.BookTypeFollowService.follow(String, String))")
    public Object follow(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String type = (String) args[1];
        Long userId = UserHolder.get();
        if ("books".equals(type)) {
            String value = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
            UserModel userModel = objectMapper.readValue(value, UserModel.class);
            long bookId = Long.parseLong((String) args[0]);
            updateModel(userModel, bookId, userId, UserModelConstant.READING.VIEW);
        }
        return joinPoint.proceed(args);
    }

    @SneakyThrows
    private void updateModel(UserModel userModel, Long bookId, Long userId, Double score) {
        boolean flag = false;
        Set<UserModel.TypeScore<Books>> bookTypes = userModel.getBookTypes();
        for (UserModel.TypeScore<Books> bookType : bookTypes) {
            if (bookType.t != null) {
                if (Objects.equals(bookType.t.getId(), bookId)) { //如果存在之前的book 直接修改model
                    bookType.score += score;
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            Books books = booksService.getById(bookId);
            books.setStructure("");
            bookTypes.add(new UserModel.TypeScore<>(books, score, 1));
        }
        userModel.setBookTypes(bookTypes);
        redisCacheUtil.set(RedisConstant.USER_MODEL + userId, objectMapper.writeValueAsString(userModel));
    }

    /**
     * 更新用户阅读模型 org.ishareReading.bankai.service.impl.BookTypeFollowService#follow(java.lang.String, java.lang.String)
     */
    @Around("execution(* org.ishareReading.bankai.service.BooksService.getBooksInfoById(Long))" +
            " || execution(* org.ishareReading.bankai.service.BooksService.getBooksInfoReadingModeById(Long, Integer, Long))" +
            " || execution(* org.ishareReading.bankai.service.BooksService.markBook(org.ishareReading.bankai.model.BookUnderlineCoordinates))" +
            " || execution(* org.ishareReading.bankai.controller.FavoriteController.doOrUndoFavorite(String, Long))")
    public Object updateUserBooksModel(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Long userId = UserHolder.get();

            try {
                Signature signature = joinPoint.getSignature();
                String methodName = signature.getName();
                String value = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
                UserModel userModel = objectMapper.readValue(value, UserModel.class);
                if(userModel == null){
                    return joinPoint.proceed();
                }
                if ("getBooksInfoById".equals(methodName)) { //阅读
                    Long bookId = (Long) joinPoint.getArgs()[0];
                    updateModel(userModel, bookId, userId, UserModelConstant.READING.VIEW);
                } else if ("getBooksInfoReadingModeById".equals(methodName)) { //进行阅读
                    Long bookId = (Long) joinPoint.getArgs()[0];
                    updateModel(userModel, bookId, userId, UserModelConstant.READING.READ);
                } else if ("markBook".equals(methodName)) {
                    BookUnderlineCoordinates underlineCoordinates = (BookUnderlineCoordinates) joinPoint.getArgs()[0];
                    Long bookId = underlineCoordinates.getBookId();
                    updateModel(userModel, bookId, userId, UserModelConstant.READING.MARK);
                } else if ("doOrUndoFavorite".equals(methodName)) {
//                        @PathVariable String type, @PathVariable Long id)
                    Object[] args = joinPoint.getArgs();
                    String type = (String) args[0];
                    Long id = (Long) args[1];
                    if ("books".equals(type)) {
                        updateModel(userModel, id, userId, UserModelConstant.READING.STAR);
                    } else if ("posts".equals(type)) {
                        updatePostsModel(userModel, id, userId, UserModelConstant.READING.STAR);
                    }
                }
            } catch (JsonProcessingException ignore) {
            }
            return joinPoint.proceed();
        } catch (Throwable ignored) {
        }
        return null;
    }

    @SneakyThrows
    private void updatePostsModel(UserModel userModel, Long postId, Long userId, Double score) {
        boolean flag = false;
        Set<UserModel.TypeScore<Posts>> postTypes = userModel.getPostTypes();
        for (UserModel.TypeScore<Posts> postsTypeScore : postTypes) {
            if (postsTypeScore.t != null) {
                if (Objects.equals(postsTypeScore.t.getId(), postId)) { //如果存在之前的book 直接修改model
                    postsTypeScore.score += UserModelConstant.POST.STAR;
                    flag = true;
                    break;
                }
            }

        }
        if (!flag) {
            Posts posts = postsService.getById(postId);
            postTypes.add(new UserModel.TypeScore<>(posts, UserModelConstant.POST.STAR, 1));
        }
        userModel.setPostTypes(postTypes);
        redisCacheUtil.set(RedisConstant.USER_MODEL + userId, objectMapper.writeValueAsString(userModel));
    }

    @SneakyThrows
    @Around("execution(* org.ishareReading.bankai.service.impl.BookHomePage.addComment(java.util.Map))")
    public Object comment(ProceedingJoinPoint joinPoint) {
        Map<String, String> map = (Map<String, String>) joinPoint.getArgs()[0];
        Long bookId = Long.parseLong(map.get("id"));
        Long userId = UserHolder.get();
        String value = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
        UserModel userModel = objectMapper.readValue(value, UserModel.class);
        updateModel(userModel, bookId, userId, UserModelConstant.READING.VIEW);
        return joinPoint.proceed();
    }

    @SneakyThrows
    @Around("execution(* org.ishareReading.bankai.service.impl.BookHomePage.addComment(java.util.Map))")
    public Object commentPosts(ProceedingJoinPoint joinPoint) {
        Map<String, String> map = (Map<String, String>) joinPoint.getArgs()[0];
        Long bookId = Long.parseLong(map.get("id"));
        Long userId = UserHolder.get();
        String value = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
        UserModel userModel = objectMapper.readValue(value, UserModel.class);
        updateModel(userModel, bookId, userId, UserModelConstant.READING.VIEW);
        return joinPoint.proceed();
    }

    @SneakyThrows
    @Around("execution(* org.ishareReading.bankai.service.PostsService.getPostInfo(Long))")
    public Object viewPosts(ProceedingJoinPoint joinPoint) {
        Long postId = (Long) joinPoint.getArgs()[0];
        Long userId = UserHolder.get();
        String value = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
        UserModel userModel = objectMapper.readValue(value, UserModel.class);
        updatePostsModel(userModel, postId, userId, UserModelConstant.READING.VIEW);
        return joinPoint.proceed();
    }


}
