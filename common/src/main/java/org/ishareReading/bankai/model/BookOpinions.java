package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 书籍见解表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("book_opinions")
public class BookOpinions extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 下划线id
     */
    @TableField("underlined_id")
    private Long underlinedId;

    /**
     * 见解内容
     */
    @TableField("opinion_text")
    private String opinionText;

    /**
     * 点赞次数
     */
    @TableField("like_count")
    private Integer likeCount;

}
