package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 文件表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("files")
public class Files  extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件path
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件名字
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件格式
     */
    @TableField("format")
    private String format;

    /**
     * 文件类型
     */
    @TableField("type")
    private String type;

    /**
     * 文件大小（字节）
     */
    @TableField("size")
    private Long size;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否公开
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 后缀名
     */
    @TableField("extension")
    private String extension;

}
