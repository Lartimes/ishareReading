package org.ishareReading.bankai.service;

public interface FollowInterface {
    String getType();

    /**
     * 关注的type 帖子类型、书籍类型 或者 user id 或者 作者名字
     *
     * @param id
     */
    void follow(String id, String type);

    /***
     * 取关
     * @param id
     */
    void unfollow(String id, String type);

}


