package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 点赞表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("likes")
public class Likes extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 目标id
     */
    @TableField("object_id")
    private Long objectId;

    /**
     * 类型：见解、书籍、评论、帖子
     */
    @TableField("type")
    private String type;

}
