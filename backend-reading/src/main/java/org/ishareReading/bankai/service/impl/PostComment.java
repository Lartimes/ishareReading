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
        return "post";
    }

    @Override
    public Response getComment(Map<String, String> map) {
        long postId = Long.parseLong(map.get("postId"));

        // Get top level comments (reply_comment_id is null)
        List<Comments> topComments = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                .eq(Comments::getPostId, postId)
                .isNull(Comments::getReplyCommentId)
                .orderByDesc(Comments::getCreateAt));

        // Get second level comments for each top comment
        for (Comments topComment : topComments) {
            List<Comments> replies = commentsMapper.selectList(new LambdaQueryWrapper<Comments>()
                    .eq(Comments::getReplyCommentId, topComment.getId())
                    .orderByAsc(Comments::getCreateAt));
            topComment.setReplies(replies);
        }

        return Response.success(topComments);
    }

    @Override
    public Response deleteComment(Map<String, String> map) {
        long commentId = Long.parseLong(map.get("id"));
        long userId = Long.parseLong(map.get("userId"));

        // Check if it's a top level comment
        Comments comment = commentsMapper.selectById(commentId);
        if (comment == null || comment.getUserId() != userId) {
            return Response.fail("No permission to delete");
        }

        if (comment.getReplyCommentId() == null) {
            // Delete all replies if it's top level
            commentsMapper.delete(new LambdaQueryWrapper<Comments>()
                    .eq(Comments::getReplyCommentId, commentId));
        }

        // Delete the comment itselffiles
        int result = commentsMapper.deleteById(commentId);
        return result > 0 ? Response.success("Deleted successfully") : Response.fail("Delete failed");
    }

    @Override
    public Response addComment(Map<String, String> map) {
        Comments comment = new Comments();
        comment.setUserId(Long.parseLong(map.get("userId")));
        comment.setPostId(Long.parseLong(map.get("postId")));
        comment.setContent(map.get("content"));

        // Set reply_comment_id if it's a reply
        String replyId = map.get("replyCommentId");
        if (replyId != null && !replyId.isEmpty()) {
            comment.setReplyCommentId(Long.parseLong(replyId));
        }

        int result = commentsMapper.insert(comment);
        return result > 0 ?
                Response.success(commentsMapper.selectById(comment.getId())) :
                Response.fail("Add comment failed");
    }
}
