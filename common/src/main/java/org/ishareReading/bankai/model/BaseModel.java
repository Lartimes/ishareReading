package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;

public class BaseModel {
     /**
     * 创建时间
     */
    @TableField("create_at")
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @TableField("update_at")
    private LocalDateTime updateAt;

    /**
     * 逻辑删除时间
     */
    @TableField("delete_at")
    private LocalDateTime deleteAt;
}
