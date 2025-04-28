package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("book_content_page")
public class BookContentPage  extends BaseModel implements Serializable {
//    id, book_id, content, pdf_page_stream , page,
//    create_at, update_at, delete_at
    private static final long serialVersionUID = 1L;


    @TableField("book_id")
    private Long bookId;

    @TableField("content")
    private String content;

    @TableField(value = "pdf_page_stream" ,exist = false)
    private byte[]  pdfPageStream;
    @TableField("page")
    private Integer page;
}
