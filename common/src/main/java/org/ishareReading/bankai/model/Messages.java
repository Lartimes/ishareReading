package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

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
     * 消息唯一ID
     */
    @TableId("id")
    private Long id;

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
     * 使用的模型
     */
    @TableField("model")
    private String model;
}
