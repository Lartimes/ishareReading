package org.ishareReading.bankai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ishareReading.bankai.config.JsonbTypeHandler;

import java.io.Serializable;

/**
 * <p>
 * agent表
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
@Getter
@Setter
@TableName("agents")
@ToString
public class Agents extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 智能体名字
     */
    @TableField("name")
    private String name;

    /**
     * 智能体头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * agent 描述
     */
    @TableField("description")
    private String description;

    /**
     * Agent 系统提示词, 用户提示
     */
    @TableField("system_prompt")
    private String systemPrompt;

    /**
     * 欢迎提示语
     */
    @TableField("welcome_message")
    private String welcomeMessage;



    /**
     * 模型配置，包含模型类型、温度等参数
     */
    @TableField(value = "model_config" , typeHandler = JsonbTypeHandler.class)
    private Object modelConfig;

    /**
     * Agent 可使用的工具列表
     */
    @TableField(value = "tools" , typeHandler = JsonbTypeHandler.class)
    private Object tools;

    /**
     * 知识库ids,json格式，配置仓库信息
     */
    @TableField("knowledge_base_ids")
    private Object knowledgeBaseIds;

    /**
     * 智能体类型， 1.聊天性 2.功能型agent
     */
    @TableField("agent_type")
    private Integer agentType;

    /**
     * 创建人用户id
     */
    @TableField("user_id")
    private Long userId;


}
