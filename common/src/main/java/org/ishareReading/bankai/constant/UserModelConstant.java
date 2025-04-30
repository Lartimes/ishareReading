package org.ishareReading.bankai.constant;

// *  阅读如何评分？
// *      定时发送，阅读中.... 3 ， 这个阅读时长应该搞一下，搞成指数或者非线性，这个很相关
// *      点赞书籍  1
// *      点评书籍  4
// *      标注书籍   1
// *      点赞书籍相关任意内容 1
// *      收藏书籍： 5
// *
// *  帖子如何评分？
// *      收藏帖子： 5
// *      点赞帖子： 1
// *      评论帖子： 3
// *      阅读帖子  0.5
public interface UserModelConstant {
    class READING {
        public static final double PING = 3;
        public static final double LIKE = 1;
        public static final double OPINION = 4;
        public static final double MARK = 1;
        public static final double STAR = 5;
    }

    class POST {
        public static final double STAR = 5;
        public static final double LIKE = 1;
        public static final double COMMENT = 3;
        public static final double VIEW = 0.5;
    }

}
