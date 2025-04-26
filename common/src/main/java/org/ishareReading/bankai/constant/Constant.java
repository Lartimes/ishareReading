package org.ishareReading.bankai.constant;

import java.util.Arrays;
import java.util.Set;

public interface Constant {
    Set<String> LIKE_ITEMS = Set.of(Arrays.toString(LIKE.values()));

    enum FILE {

    }

    enum BOOK {

    }

    enum POST {

    }

    enum LIKE {
        //          帖子 ，评论， 书籍，见解
        POST, OPINION, BOOK, COMMENT
    }
}
