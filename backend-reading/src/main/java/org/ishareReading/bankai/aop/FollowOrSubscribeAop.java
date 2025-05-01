package org.ishareReading.bankai.aop;

import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.response.ResponseCode;
import org.ishareReading.bankai.service.FollowInterface;
import org.ishareReading.bankai.service.impl.AuthorFollowService;
import org.ishareReading.bankai.service.impl.BookTypeFollowService;
import org.ishareReading.bankai.service.impl.PostFollowService;
import org.ishareReading.bankai.service.impl.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * app context，切面 类似于SPI机制效果 或者说， 策略模式
 */
@Service
public class FollowOrSubscribeAop {
    @Autowired
    private ApplicationContext applicationContext;

    public void followOrUnfollow(Map<String, String> map) {
        String type = map.get("type");
        String id = map.get("id");
        Map<String, FollowInterface> beansOfType = applicationContext.getBeansOfType(FollowInterface.class);
        if (beansOfType.isEmpty()) {
            throw new BusinessException(ResponseCode.SERVER_ERROR);
        }
        Collection<FollowInterface> values = beansOfType.values();
        for (FollowInterface followInterface : values) {
            if (followInterface.getType().equals(type)) {
                boolean flag = isFlag(followInterface);
                if (flag) {
                    if (map.get("do").equals("true")) {
                        solve1(followInterface, id, type);
                    } else {
                        solve2(followInterface, id, type);
                    }

                    return;
                }
            }
        }
        throw new BusinessException(ResponseCode.SERVER_ERROR);
    }

    private static boolean isFlag(FollowInterface followInterface) {
        boolean flag = false;
        if (followInterface instanceof AuthorFollowService authorFollowService) {
            flag = true;
        } else if (followInterface instanceof BookTypeFollowService bookTypeFollowService) {
            flag = true;
        } else if (followInterface instanceof UserFollowService userFollowService) {
            flag = true;
        } else if (followInterface instanceof PostFollowService postFollowService) {
            flag = true;
        }
        return flag;
    }

    private void solve1(FollowInterface followInterface, String id, String type) {
        followInterface.follow(id, type);
    }

    private void solve2(FollowInterface followInterface, String id, String type) {
        followInterface.unfollow(id, type);
    }


}
