package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * messages表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("messages")
public class Messages extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 所属会话ID
     */
    @TableField("session_id")
    private Long sessionId;

    /**
     * 消息角色(user/assistant/system)
     */
    @TableField("role")
    private String role;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * Token数量(可选，用于统计)
     */
    @TableField("token_count")
    private Integer tokenCount;


    /**
     * 消息是否活跃 ？？ 间隔时间 ， token数量？
     *  用户多少？
     *  model 多少？
     */
    @TableField("actived")
    private Boolean actived;
}
