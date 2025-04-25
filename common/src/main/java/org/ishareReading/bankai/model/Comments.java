package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 评论表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("comments")
public class Comments extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 帖子id
     */
    @TableField("post_id")
    private Long postId;

    /**
     * 回复评论id
     */
    @TableField("reply_comment_id")
    private Long replyCommentId;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 点赞次数
     */
    @TableField("like_count")
    private Integer likeCount;

}
