package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * messgae_groups表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("messgae_groups")
public class MessgaeGroups  extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 消息组名称
     */
    @TableField("name")
    private String name;

    /**
     * 消息组描述
     */
    @TableField("description")
    private String description;

    /**
     * 所属会话ID
     */
    @TableField("session_id")
    private Long sessionId;

    /**
     * 是否活跃
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 创建人ID
     */
    @TableField("user_id")
    private Long userId;

}
