package org.ishareReading.bankai.constant;

public interface RedisConstant {

    String CAPTCHA = "captcha:";


    // 用户关注人
    String USER_FOLLOW = "user:follow:";

    // 用户粉丝
    String USER_FANS = "user:fans:";

    // 关注的帖子
    String POST_FOLLOW = "post::follow";

    // 关注某帖子的用户
    String POST_FANS = "post::fans";


    // 关注的书类型
    String BOOK_TYPE_FOLLOW = "book_type::follow";

    // 书类型的受众
    String BOOK_TYPE_FANS = "book_type::fans";

    // 用户关注的作者
    String AUTHOR_FOLLOW = "author::follow";

    // 作者的粉丝
    String AUTHOR_FANS = "author::fans";


    String LIKE_PREFIX = "like";
    String LIKE_COUNT = "like:count";

    String LIKE_LISTENER = "like:listener:";


}
