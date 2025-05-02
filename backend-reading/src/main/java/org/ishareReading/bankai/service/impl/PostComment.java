package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.mapper.CommentsMapper;
import org.ishareReading.bankai.model.Comments;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.CommentInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/// 还有帖子下面的评论comments
@Service
public class PostComment implements CommentInterface {
    @Autowired
    private CommentsMapper commentsMapper;

    @Override
    public String getType() {
        return "posts";
    }

    /**
     * 只做二级
     */
    @Override
    public Response getComment(Map<String, String> map) {
        long postId = Long.parseLong(map.get("id"));

        List<Comments> topComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getPostId, postId)
                        .and(wrapper -> wrapper.isNull(Comments::getReplyCommentId))
                        .select(Comments::getId, Comments::getReplyCommentId , Comments::getLikeCount ,
                                Comments::getContent ,Comments::getCreateAt , Comments::getUserId )
                .orderByDesc(Comments::getCreateAt));


        for (Comments topComment : topComments) {
            List<Comments> replies = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                    .eq(Comments::getReplyCommentId, topComment.getId())
                    .select(Comments::getId, Comments::getReplyCommentId , Comments::getLikeCount ,
                                Comments::getContent ,Comments::getCreateAt , Comments::getUserId )
                    .orderByAsc(Comments::getCreateAt));
            topComment.setReplies(replies);
        }

        return Response.success(topComments);
    }

    @Override
    public Response deleteComment(Map<String, String> map) {
        long commentId = Long.parseLong(map.get("id"));
        long userId = Long.parseLong(map.get("userId"));

        Comments comment = commentsMapper.selectById(commentId);
        if (comment == null || comment.getUserId() != userId) {
            return Response.fail("No permission to delete");
        }

        if (comment.getReplyCommentId() == null) {
            commentsMapper.delete(new LambdaQueryWrapper<Comments>()
                    .eq(Comments::getReplyCommentId, commentId));
        }

        int result = commentsMapper.deleteById(commentId);
        return result > 0 ? Response.success("Deleted successfully") : Response.fail("Delete failed");
    }

    @Override
    public Response addComment(Map<String, String> map) {
        Comments comment = new Comments();
        comment.setUserId(Long.parseLong(map.get("userId")));
        comment.setPostId(Long.parseLong(map.get("id")));
        comment.setContent(map.get("text"));

        String replyId = map.get("replyCommentId");
        if (replyId != null && !replyId.isEmpty()) {
            comment.setReplyCommentId(Long.parseLong(replyId));
        }

        int result = commentsMapper.insert(comment);
        return result > 0 ?
                Response.success("添加成功") :
                Response.fail("Add comment failed");
    }
}
