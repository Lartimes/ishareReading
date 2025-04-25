package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 上下文表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("contexts")
public class Contexts  extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 会话id
     */
    @TableField("session_id")
    private Long sessionId;

    /**
     * 活跃列表ids
     */
    @TableField("activate_messages")
    private Object activateMessages;

    /**
     * 过去N条消息的摘要
     */
    @TableField("summary")
    private String summary;

}
