package org.ishareReading.bankai.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.model.Posts;
import org.ishareReading.bankai.model.UserModel;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.service.AgentsService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LoginListener {

    private static final Random RANDOM = new Random();
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgentsService agentsService;

    /**
     *  1.初始化模型
     * 或者
     *  2.更新上次登录的模型，进行更新，只留下最感兴趣的几个
     *
     * @param users
     */
    @SneakyThrows
    @EventListener(classes = Users.class)
    public void onLogin(Users users) {
        System.out.println("监听到登陆事件");
        Long userId = users.getId();
        String userModelStr = redisCacheUtil.getKey(RedisConstant.USER_MODEL + userId);
        System.out.println(userModelStr);
        if (userModelStr.equals("null")) {
            UserModel userModel = new UserModel();
            userModel.setReadingTime(RANDOM.nextInt(1000, 5000));
            userModel.setReadingFrequency(RANDOM.nextInt(1, 7));
            userModelStr = objectMapper.writeValueAsString(userModel);
            redisCacheUtil.set(RedisConstant.USER_MODEL + userId, userModelStr);
            return;
        }
        UserModel userModel = objectMapper.readValue(userModelStr, UserModel.class);
        if(userModel == null){
            return;
        }
        userModel.setReadingFrequency(userModel.getReadingFrequency() + 1 > 7 ? 7 : userModel.getReadingFrequency());
        Set<UserModel.TypeScore<Posts>> set1 = userModel.getPostTypes();
        int size = set1.size();
        UserModel.TypeScore<Posts> tmp = set1.stream().skip(size >> 2).findFirst().orElse(null);
        if (tmp != null) {
            set1 = set1.stream().filter(a -> a.score > tmp.score)
                    .collect(Collectors.toSet());
        }
        Set<UserModel.TypeScore<Books>> set2 = userModel.getBookTypes();
        size = set2.size();
        UserModel.TypeScore<Books> destBook = set2.stream().skip(size >> 2).findFirst().orElse(null);
        if (tmp != null) {
            set2 = set2.stream().filter(a -> a.score > tmp.score)
                    .collect(Collectors.toSet());
        }
        userModel.setBookTypes(set2);
        userModel.setPostTypes(set1);
        redisCacheUtil.set(RedisConstant.USER_MODEL + userId, objectMapper.writeValueAsString(userModel));
        //注册agents
        agentsService.registerAgentsByUserId(userId);
    }
}
