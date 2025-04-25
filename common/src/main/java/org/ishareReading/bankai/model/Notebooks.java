package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * notebook 笔记表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("notebooks")
public class Notebooks  extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 书籍id，可为空
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 笔记名字
     */
    @TableField("note_name")
    private String noteName;

    /**
     * 笔记内容
     */
    @TableField("note_content")
    private String noteContent;

    /**
     * 文件表id
     */
    @TableField("file_id")
    private Long fileId;

}
