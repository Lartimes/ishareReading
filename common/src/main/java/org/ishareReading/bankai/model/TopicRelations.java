package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * topic-relations表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("topic_relations")
public class TopicRelations extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 父话题ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 子话题ID
     */
    @TableField("child_id")
    private Long childId;

    /**
     * 话题名字
     */
    @TableField("topic_name")
    private String topicName;

}
