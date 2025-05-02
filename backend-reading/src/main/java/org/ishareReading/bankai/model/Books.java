package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ishareReading.bankai.config.MapTypeHandler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 书籍表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("books")
@ToString
public class Books extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 书籍name
     */
    @TableField("name")
    private String name;

    /**
     * 书籍的作者，可能存在多位作者，可使用特定分隔符区分
     */
    @TableField("author")
    private String author;

    /**
     * 出版年份
     */
    @TableField("publication_year")
    private Integer publicationYear;

    /**
     * 出版社
     */
    @TableField("publisher")
    private String publisher;

    /**
     * 唯一标识号
     */
    @TableField("isbn")
    private String isbn;

    /**
     * 书籍类型，关联type表
     */
    @TableField("genre")
    private String genre;
    public   String[] getGenreSplit(){
        return genre.split(",");
    }
    /**
     * 简介
     */
    @TableField("description")
    private String description;

    /**
     * 封面id，关联fileid
     */
    @TableField("cover_image_id")
    private Long coverImageId;

    /**
     * 总页数
     */
    @TableField("total_pages")
    private Integer totalPages;

    /**
     * 语言
     */
    @TableField("language")
    private String language;

    /**
     * 评分
     */
    @TableField("average_rating")
    private BigDecimal averageRating;

    /**
     * 用户评价总数改为 点赞数,
     */
    @TableField("rating_count")
    private Integer ratingCount;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 下载量
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件id
     */
    @TableField("file_id")
    private Long fileId;

    @TableField(value = "structure" , typeHandler = MapTypeHandler.class)
    private Object structure;


}
