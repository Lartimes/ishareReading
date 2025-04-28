package org.ishareReading.bankai.aop;

import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.response.ResponseCode;
import org.ishareReading.bankai.service.CommentInterface;
import org.ishareReading.bankai.service.impl.BookHomePage;
import org.ishareReading.bankai.service.impl.PostComment;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Service
public class CommentAop {

    /// /            book_id , text , type = book / 还有帖子下面的评论comments / post_id , reply_id , text ,type= / 也同上
    private static final Set<String> POST_COMMENTS = Set.of("type", "userId", "postId", "text"); //"replyCommentId"
    private static final Set<String> BOOK_COMMENTS = Set.of("type", "userId", "bookId", "text");


    //    //    书籍的评论门户首页首页 book_opinions
    private final ApplicationContext applicationContext;

    public CommentAop(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Response comment(Map<String, String> map) {
        String type = map.get("type");
        Map<String, CommentInterface> beansOfType = applicationContext.getBeansOfType(CommentInterface.class);
        if (beansOfType.isEmpty()) {
            throw new BusinessException(ResponseCode.SERVER_ERROR);
        }

        Collection<CommentInterface> values = beansOfType.values();
        for (CommentInterface commentInterface : values) {
            if (commentInterface.getType().equals(type)) {
                if (commentInterface instanceof PostComment postComment) {
                    if (!map.keySet().containsAll(POST_COMMENTS)) {
                        throw new BusinessException(ResponseCode.FAILURE);
                    }
                    return postComment.addComment(map);
                } else if (commentInterface instanceof BookHomePage homePage) {
                    if (!map.keySet().containsAll(BOOK_COMMENTS)) {
                        throw new BusinessException(ResponseCode.FAILURE);
                    }
                    return homePage.addComment(map);
                }
            }
        }
        throw new BusinessException(ResponseCode.SERVER_ERROR);
    }

    public Response delComment(Map<String, String> map) {
        String type = map.get("type");
        Map<String, CommentInterface> beansOfType = applicationContext.getBeansOfType(CommentInterface.class);
        if (beansOfType.isEmpty()) {
            throw new BusinessException(ResponseCode.SERVER_ERROR);
        }

        Collection<CommentInterface> values = beansOfType.values();
        for (CommentInterface commentInterface : values) {
            if (commentInterface.getType().equals(type)) {
                return commentInterface.deleteComment(map);
            }
        }
        throw new BusinessException(ResponseCode.SERVER_ERROR);
    }

    public Response getComment(Map<String, String> map) {
         String type = map.get("type");
        Map<String, CommentInterface> beansOfType = applicationContext.getBeansOfType(CommentInterface.class);
        if (beansOfType.isEmpty()) {
            throw new BusinessException(ResponseCode.SERVER_ERROR);
        }

        Collection<CommentInterface> values = beansOfType.values();
        for (CommentInterface commentInterface : values) {
            if (commentInterface.getType().equals(type)) {
                return commentInterface.getComment(map);
            }
        }
        throw new BusinessException(ResponseCode.SERVER_ERROR);
    }
}
