package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * message-group-tags表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("message_group_tags")
public class MessageGroupTags extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签唯一ID
     */
    @TableId("id")
    private Long id;

    /**
     * 消息组ID
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

}
