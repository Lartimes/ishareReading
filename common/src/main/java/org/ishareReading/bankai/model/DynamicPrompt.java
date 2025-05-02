package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dynamic_prompt")
public class DynamicPrompt {
    @TableField("keywords")
    private String keywords;

    @TableField("prompt_template")
    private String promptTemplate;
}
