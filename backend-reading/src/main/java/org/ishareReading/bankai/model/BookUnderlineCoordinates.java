package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 书籍下环线坐标表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("book_underline_coordinates")
public class BookUnderlineCoordinates extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;

    /**
     * 书籍id
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 页数
     */
    @TableField("page_number")
    private Integer pageNumber;

    /**
     * 起始横坐标
     */
    @TableField("start_x")
    private BigDecimal startX;

    /**
     * 起始纵坐标
     */
    @TableField("start_y")
    private BigDecimal startY;

    /**
     * 结束横坐标
     */
    @TableField("end_x")
    private BigDecimal endX;

    /**
     * 结束纵坐标
     */
    @TableField("end_y")
    private BigDecimal endY;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 相对xpath下环线标注超链接样式
     */
    @TableField("relative_xpath")
    private String relativeXpath;


}
