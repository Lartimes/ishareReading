package org.ishareReading.bankai.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * app context，切面 类似于SPI机制效果 或者说， 策略模式
 */
@Service
public class FollowOrSubscribeAop {
    @Autowired
    private ApplicationContext applicationContext;
//    todo 切面接口
//     根据type 决定走哪一个关注取关器


}
