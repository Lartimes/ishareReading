package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ishareReading.bankai.config.VectorTypeHandler;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("knowledge_base")
public class KnowledgeBase extends BaseModel implements Serializable {

    @TableField("summary")
    private String summary;

    @TableField("content")
    private String content;

    @TableField(value = "embedding" , typeHandler = VectorTypeHandler.class)
    private float[] embedding;
}
