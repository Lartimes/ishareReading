package org.ishareReading.bankai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.constant.UserModelConstant;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.model.UserModel;
import org.ishareReading.bankai.service.BooksService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

/**
 * 用户模型相关的接口
 *  阅读如何评分？
 *      定时发送，阅读中.... 3 ， 这个阅读时长应该搞一下，搞成指数或者非线性，这个很相关
 *      点赞书籍  1
 *      点评书籍  4
 *      标注书籍   1
 *      点赞书籍相关任意内容 1
 *      收藏书籍： 5
 *
 *  帖子如何评分？
 *      收藏帖子： 5
 *      点赞帖子： 1
 *      评论帖子： 3
 *      阅读帖子 0.5
 */
@RestController
@RequestMapping("/userModel")
public class UserModelController {
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BooksService booksService;



//   TODO 推送帖子和书籍和其他用户




    /**
     * 阅读时更新用户模型
     * 登陆的时候就要初始化模型了
     * 阅读界面，前端定时ping
     *
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
            if(bookType.t.getId().equals(bookId)) {
                bookType.time +=5;
                bookType.score = bookType.score+ UserModelConstant.READING.PING;
                flag = true;
            }
        }
        if(!flag){
            UserModel.TypeScore<Books> booksTypeScore = new UserModel.TypeScore<>();
            booksTypeScore.t = booksService.getById(bookId);
            booksTypeScore.time =5;
            booksTypeScore.score= 5.0;
            booksTypeScore.readDate = LocalDate.now();
            userModel.getBookTypes().add(booksTypeScore);
        }
        redisCacheUtil.set(RedisConstant.USER_MODEL + userId, objectMapper.writeValueAsString(userModel));
    }




}
