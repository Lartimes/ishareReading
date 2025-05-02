package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * sessions表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("sessions")
public class Sessions extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 会话标题
     */
    @TableField("title")
    private String title;

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联的agentId
     */
    @TableField("agent_id")
    private Long agentId;

    /**
     * 是否归档
     */
    @TableField("archived")
    private Boolean archived;

    /**
     * 会话描述
     */
    @TableField("description")
    private String description;

    /**
     * 会话元数据，可存储其他自定义信息
     */
    @TableField("metadata")
    private Object metadata;

    /**
     * 摘要
     */
    @TableField("summary")
    private String summary;

}
