package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * message-group-items表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("message_group_items")
public class MessageGroupItems extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联唯一ID
     */
    @TableId("id")
    private Long id;

    /**
     * 消息组topic ID
     */
    @TableField("group_tags_id")
    private Long groupTagsId;

    /**
     * 消息ID
     */
    @TableField("message_id")
    private Long messageId;

    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

}
