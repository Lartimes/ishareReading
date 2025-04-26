package org.ishareReading.bankai.service.impl;

import org.springframework.stereotype.Service;

//TODO
public interface FollowInterface {

    /**
     * 关注的type 帖子类型、书籍类型 或者 user id 或者 作者名字
     *
     * @param id
     */
    void follow(String type, String id);

    /***
     * 取关
     * @param type
     * @param id
     */
    void unfollow(String type, String id);

}
//帖子类型关注取关ID
@Service
class PostFollowService implements FollowInterface {
    @Override
    public void follow(String type, String id) {

    }

    @Override
    public void unfollow(String type, String id) {

    }
}

//用户关注取关
@Service
class  UserFollowService implements FollowInterface {
    @Override
    public void follow(String type, String id) {

    }

    @Override
    public void unfollow(String type, String id) {

    }
}
//书籍类型
@Service
class BookTypeFollowService implements FollowInterface {
    @Override
    public void follow(String type, String id) {

    }

    @Override
    public void unfollow(String type, String id) {

    }
}
//作者关注
@Service
class AuthorFollowService implements FollowInterface {
    @Override
    public void follow(String type, String id) {

    }

    @Override
    public void unfollow(String type, String id) {

    }
}

