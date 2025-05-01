package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * type
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("types")
public class Types extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 名字
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 类型：书籍、帖子、文件
     */
    @TableField("type")
    private String type;


    @TableField("embedding")
    private Double[] embedding;

    public void setEmbedding(float[] embedding) {
        Double[] doubleEmbedding = new Double[embedding.length];
        for (int i = 0; i < embedding.length; i++) {
            doubleEmbedding[i] = (double) embedding[i];
        }
        this.embedding = doubleEmbedding;
    }

    public void setEmbedding(Double[] embedding) {
        this.embedding = embedding;
    }
}
