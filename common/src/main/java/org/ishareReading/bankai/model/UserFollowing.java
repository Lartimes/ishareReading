package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 用户关注表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("user_following")
public class UserFollowing  extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关注的一类ID 可为用户id 、 书籍类型、作者、帖子类型等
     */
    @TableField("follow_id")
    private String followId;

    /**
     * 用户、 书籍类型、作者、帖子类型分区等
     */
    @TableField("type")
    private String type;


    /**
     * type表详细信息id
     */
    @TableField("type_id")
    private String typeId;



}
