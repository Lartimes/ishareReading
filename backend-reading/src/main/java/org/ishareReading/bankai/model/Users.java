package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("users")
@ToString
public class Users extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 邮箱
     */
    @TableField("email")
    @Email
    private String email;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;


    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 自我介绍
     */
    @TableField("self_intro")
    private String selfIntro;

    /**
     * 性别
     */
    @TableField("sex")
    private String sex;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 用户阅读偏好
     */
    @TableField("reading_preference")
    private Object readingPreference;

    /**
     * 上次登录时间，用于检索
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField(exist = false)
    private String avatarBase64;
}
