package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class BaseModel {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
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
