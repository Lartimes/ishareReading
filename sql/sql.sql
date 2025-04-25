-- 文件表
CREATE TABLE files (
    id BIGINT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    format VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    size BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_files PRIMARY KEY (id)
);

COMMENT ON TABLE files IS '文件表';
COMMENT ON COLUMN files.id IS '主键';
COMMENT ON COLUMN files.file_path IS '文件path';
COMMENT ON COLUMN files.file_name IS '文件名字';
COMMENT ON COLUMN files.format IS '文件格式';
COMMENT ON COLUMN files.type IS '文件类型';
COMMENT ON COLUMN files.size IS '文件大小（字节）';
COMMENT ON COLUMN files.user_id IS '用户id';
COMMENT ON COLUMN files.is_public IS '是否公开';
COMMENT ON COLUMN files.create_at IS '创建时间';
COMMENT ON COLUMN files.update_at IS '更新时间';
COMMENT ON COLUMN files.delete_at IS '逻辑删除时间';

-- 用户表
CREATE TABLE users (
    id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    user_name VARCHAR(50) NOT NULL,
    account VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    self_intro TEXT,
    sex VARCHAR(10),
    avatar VARCHAR(255),
    reading_preference JSONB,
    last_login_time TIMESTAMP WITHOUT TIME ZONE,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '主键';
COMMENT ON COLUMN users.email IS '邮箱';
COMMENT ON COLUMN users.user_name IS '用户名';
COMMENT ON COLUMN users.account IS '账户';
COMMENT ON COLUMN users.password IS '密码';
COMMENT ON COLUMN users.self_intro IS '自我介绍';
COMMENT ON COLUMN users.sex IS '性别';
COMMENT ON COLUMN users.avatar IS '头像';
COMMENT ON COLUMN users.reading_preference IS '用户阅读偏好';
COMMENT ON COLUMN users.last_login_time IS '上次登录时间，用于检索';
COMMENT ON COLUMN users.create_at IS '创建时间';
COMMENT ON COLUMN users.update_at IS '更新时间';
COMMENT ON COLUMN users.delete_at IS '逻辑删除时间';

-- 用户关注表
CREATE TABLE user_following (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    follow_id BIGINT NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_user_following PRIMARY KEY (id)
);

COMMENT ON TABLE user_following IS '用户关注表';
COMMENT ON COLUMN user_following.id IS '主键';
COMMENT ON COLUMN user_following.user_id IS '用户id';
COMMENT ON COLUMN user_following.follow_id IS '关注的用户id';
COMMENT ON COLUMN user_following.create_at IS '创建时间';
COMMENT ON COLUMN user_following.update_at IS '更新时间';
COMMENT ON COLUMN user_following.delete_at IS '逻辑删除时间';

-- 帖子表
CREATE TABLE posts (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    book_id BIGINT,
    type_id BIGINT,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    start_count INT DEFAULT 0,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

COMMENT ON TABLE posts IS '帖子表';
COMMENT ON COLUMN posts.id IS '主键';
COMMENT ON COLUMN posts.user_id IS '用户id';
COMMENT ON COLUMN posts.title IS '标题';
COMMENT ON COLUMN posts.content IS '帖子内容';
COMMENT ON COLUMN posts.book_id IS '关联的书籍，可为null';
COMMENT ON COLUMN posts.type_id IS '帖子类型id';
COMMENT ON COLUMN posts.view_count IS '浏览总数';
COMMENT ON COLUMN posts.like_count IS '点赞次数';
COMMENT ON COLUMN posts.start_count IS '收藏次数';
COMMENT ON COLUMN posts.create_at IS '创建时间';
COMMENT ON COLUMN posts.update_at IS '更新时间';
COMMENT ON COLUMN posts.delete_at IS '逻辑删除时间';

-- 评论表
CREATE TABLE comments (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    reply_comment_id BIGINT,
    content TEXT NOT NULL,
    like_count INT DEFAULT 0,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

COMMENT ON TABLE comments IS '评论表';
COMMENT ON COLUMN comments.id IS '主键';
COMMENT ON COLUMN comments.user_id IS '用户id';
COMMENT ON COLUMN comments.post_id IS '帖子id';
COMMENT ON COLUMN comments.reply_comment_id IS '回复评论id';
COMMENT ON COLUMN comments.content IS '内容';
COMMENT ON COLUMN comments.like_count IS '点赞次数';
COMMENT ON COLUMN comments.create_at IS '创建时间';
COMMENT ON COLUMN comments.update_at IS '更新时间';
COMMENT ON COLUMN comments.delete_at IS '逻辑删除时间';

-- 书籍表
CREATE TABLE books (
    id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    publication_year INT,
    publisher VARCHAR(255),
    isbn VARCHAR(20),
    genre VARCHAR(50),
    description TEXT,
    cover_image_id BIGINT,
    total_pages INT,
    language VARCHAR(20),
    average_rating NUMERIC(3, 2),
    rating_count INT,
    view_count INT,
    download_count INT,
    upload_time TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT,
    file_id BIGINT,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

COMMENT ON TABLE books IS '书籍表';
COMMENT ON COLUMN books.id IS '主键';
COMMENT ON COLUMN books.name IS '书籍name';
COMMENT ON COLUMN books.author IS '书籍的作者，可能存在多位作者，可使用特定分隔符区分';
COMMENT ON COLUMN books.publication_year IS '出版年份';
COMMENT ON COLUMN books.publisher IS '出版社';
COMMENT ON COLUMN books.isbn IS '唯一标识号';
COMMENT ON COLUMN books.genre IS '书籍类型，关联type表';
COMMENT ON COLUMN books.description IS '简介';
COMMENT ON COLUMN books.cover_image_id IS '封面id，关联fileid';
COMMENT ON COLUMN books.total_pages IS '总页数';
COMMENT ON COLUMN books.language IS '语言';
COMMENT ON COLUMN books.average_rating IS '评分';
COMMENT ON COLUMN books.rating_count IS '用户评价总数';
COMMENT ON COLUMN books.view_count IS '浏览量';
COMMENT ON COLUMN books.download_count IS '下载量';
COMMENT ON COLUMN books.upload_time IS '上传时间';
COMMENT ON COLUMN books.user_id IS '用户id';
COMMENT ON COLUMN books.file_id IS '文件id';
COMMENT ON COLUMN books.create_at IS '创建时间';
COMMENT ON COLUMN books.update_at IS '更新时间';
COMMENT ON COLUMN books.delete_at IS '逻辑删除时间';

-- 书籍下环线坐标表
CREATE TABLE book_underline_coordinates (
    id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    page_number INT NOT NULL,
    start_x NUMERIC(6, 2) NOT NULL,
    start_y NUMERIC(6, 2) NOT NULL,
    end_x NUMERIC(6, 2) NOT NULL,
    end_y NUMERIC(6, 2) NOT NULL,
    user_id BIGINT NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_book_underline_coordinates PRIMARY KEY (id)
);

COMMENT ON TABLE book_underline_coordinates IS '书籍下环线坐标表';
COMMENT ON COLUMN book_underline_coordinates.id IS '主键';
COMMENT ON COLUMN book_underline_coordinates.book_id IS '书籍id';
COMMENT ON COLUMN book_underline_coordinates.page_number IS '页数';
COMMENT ON COLUMN book_underline_coordinates.start_x IS '起始横坐标';
COMMENT ON COLUMN book_underline_coordinates.start_y IS '起始纵坐标';
COMMENT ON COLUMN book_underline_coordinates.end_x IS '结束横坐标';
COMMENT ON COLUMN book_underline_coordinates.end_y IS '结束纵坐标';
COMMENT ON COLUMN book_underline_coordinates.user_id IS '用户id';
COMMENT ON COLUMN book_underline_coordinates.create_at IS '创建时间';
COMMENT ON COLUMN book_underline_coordinates.update_at IS '更新时间';
COMMENT ON COLUMN book_underline_coordinates.delete_at IS '逻辑删除时间';

-- 书籍见解表
CREATE TABLE book_opinions (
    id BIGINT NOT NULL,
    underlined_id BIGINT NOT NULL,
    opinion_text TEXT NOT NULL,
    like_count INT DEFAULT 0,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_book_opinions PRIMARY KEY (id)
);

COMMENT ON TABLE book_opinions IS '书籍见解表';
COMMENT ON COLUMN book_opinions.id IS '主键';
COMMENT ON COLUMN book_opinions.underlined_id IS '下划线id';
COMMENT ON COLUMN book_opinions.opinion_text IS '见解内容';
COMMENT ON COLUMN book_opinions.like_count IS '点赞次数';
COMMENT ON COLUMN book_opinions.create_at IS '创建时间';
COMMENT ON COLUMN book_opinions.update_at IS '更新时间';
COMMENT ON COLUMN book_opinions.delete_at IS '逻辑删除时间';

-- notebook 笔记表
CREATE TABLE notebooks (
    id BIGINT NOT NULL,
    book_id BIGINT,
    user_id BIGINT NOT NULL,
    note_name VARCHAR(255),
    note_content TEXT,
    file_id BIGINT,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_notebooks PRIMARY KEY (id)
);

COMMENT ON TABLE notebooks IS 'notebook 笔记表';
COMMENT ON COLUMN notebooks.id IS '主键';
COMMENT ON COLUMN notebooks.book_id IS '书籍id，可为空';
COMMENT ON COLUMN notebooks.user_id IS '用户id';
COMMENT ON COLUMN notebooks.note_name IS '笔记名字';
COMMENT ON COLUMN notebooks.note_content IS '笔记内容';
COMMENT ON COLUMN notebooks.file_id IS '文件表id';
COMMENT ON COLUMN notebooks.create_at IS '创建时间';
COMMENT ON COLUMN notebooks.update_at IS '更新时间';
COMMENT ON COLUMN notebooks.delete_at IS '逻辑删除时间';

-- 点赞表
CREATE TABLE likes (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_likes PRIMARY KEY (id)
);

COMMENT ON TABLE likes IS '点赞表';
COMMENT ON COLUMN likes.id IS '主键';
COMMENT ON COLUMN likes.user_id IS '用户id';
COMMENT ON COLUMN likes.object_id IS '目标id';
COMMENT ON COLUMN likes.type IS '类型：见解、书籍、评论、帖子';
COMMENT ON COLUMN likes.create_at IS '创建时间';
COMMENT ON COLUMN likes.update_at IS '更新时间';
COMMENT ON COLUMN likes.delete_at IS '逻辑删除时间';

-- 收藏表
CREATE TABLE favorites (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_favorites PRIMARY KEY (id)
);

COMMENT ON TABLE favorites IS '收藏表';
COMMENT ON COLUMN favorites.id IS '主键';
COMMENT ON COLUMN favorites.user_id IS '用户id';
COMMENT ON COLUMN favorites.object_id IS '目标id';
COMMENT ON COLUMN favorites.type IS '类型：书籍、帖子';
COMMENT ON COLUMN favorites.create_at IS '创建时间';
COMMENT ON COLUMN favorites.update_at IS '更新时间';
COMMENT ON COLUMN favorites.delete_at IS '逻辑删除时间';

-- 书籍类型表
CREATE TABLE book_types (
    id BIGINT NOT NULL,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_book_types PRIMARY KEY (id)
);

COMMENT ON TABLE book_types IS '书籍类型表';
COMMENT ON COLUMN book_types.id IS '主键';
COMMENT ON COLUMN book_types.type_name IS '名字';
COMMENT ON COLUMN book_types.type IS '类型：书籍、帖子';
COMMENT ON COLUMN book_types.create_at IS '创建时间';
COMMENT ON COLUMN book_types.update_at IS '更新时间';
COMMENT ON COLUMN book_types.delete_at IS '逻辑删除时间';

-- agent表
CREATE TABLE agents (
    id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    avatar VARCHAR(255),
    description TEXT,
    system_prompt TEXT,
    welcome_message TEXT,
    model_config JSONB,
    tools JSONB,
    knowledge_base_ids JSONB,
    agent_type INT,
    user_id BIGINT,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_agents PRIMARY KEY (id)
);

COMMENT ON TABLE agents IS 'agent表';
COMMENT ON COLUMN agents.id IS '主键';
COMMENT ON COLUMN agents.name IS '智能体名字';
COMMENT ON COLUMN agents.avatar IS '智能体头像';
COMMENT ON COLUMN agents.description IS 'agent 描述';
COMMENT ON COLUMN agents.system_prompt IS 'Agent 系统提示词';
COMMENT ON COLUMN agents.welcome_message IS '欢迎提示语';
COMMENT ON COLUMN agents.model_config IS '模型配置，包含模型类型、温度等参数';
COMMENT ON COLUMN agents.tools IS 'Agent 可使用的工具列表';
COMMENT ON COLUMN agents.knowledge_base_ids IS '知识库ids,json格式，配置仓库信息';
COMMENT ON COLUMN agents.agent_type IS '智能体类型， 1.聊天性 2.功能型agent';
COMMENT ON COLUMN agents.user_id IS '创建人用户id';
COMMENT ON COLUMN agents.create_at IS '创建时间';
COMMENT ON COLUMN agents.update_at IS '更新时间';
COMMENT ON COLUMN agents.delete_at IS '逻辑删除时间';

-- 上下文表
CREATE TABLE contexts (
    id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    activate_messages JSONB,
    summary TEXT,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_contexts PRIMARY KEY (id)
);

COMMENT ON TABLE contexts IS '上下文表';
COMMENT ON COLUMN contexts.id IS '主键';
COMMENT ON COLUMN contexts.session_id IS '会话id';
COMMENT ON COLUMN contexts.activate_messages IS '活跃列表ids';
COMMENT ON COLUMN contexts.summary IS '过去N条消息的摘要';
COMMENT ON COLUMN contexts.create_at IS '创建时间';
COMMENT ON COLUMN contexts.update_at IS '更新时间';
COMMENT ON COLUMN contexts.delete_at IS '逻辑删除时间';

-- sessions表
CREATE TABLE sessions (
    id BIGINT NOT NULL,
    title VARCHAR(255),
    user_id BIGINT NOT NULL,
    agent_id BIGINT,
    is_archived BOOLEAN DEFAULT FALSE,
    description TEXT,
    metadata JSONB,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

COMMENT ON TABLE sessions IS'sessions表';
COMMENT ON COLUMN sessions.id IS '会话唯一ID';
COMMENT ON COLUMN sessions.title IS '会话标题';
COMMENT ON COLUMN sessions.user_id IS '所属用户ID';
COMMENT ON COLUMN sessions.agent_id IS '关联的agentId';
COMMENT ON COLUMN sessions.is_archived IS '是否归档';
COMMENT ON COLUMN sessions.description IS '会话描述';
COMMENT ON COLUMN sessions.metadata IS '会话元数据，可存储其他自定义信息';
COMMENT ON COLUMN sessions.create_at IS '创建时间';
COMMENT ON COLUMN sessions.update_at IS '更新时间';
COMMENT ON COLUMN sessions.delete_at IS '逻辑删除时间';

-- messages表
CREATE TABLE messages (
    id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    token_count INT,
    model VARCHAR(50),
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_messages PRIMARY KEY (id)
);

COMMENT ON TABLE messages IS 'messages表';
COMMENT ON COLUMN messages.id IS '消息唯一ID';
COMMENT ON COLUMN messages.session_id IS '所属会话ID';
COMMENT ON COLUMN messages.role IS '消息角色(user/assistant/system)';
COMMENT ON COLUMN messages.content IS '消息内容';
COMMENT ON COLUMN messages.token_count IS 'Token数量(可选，用于统计)';
COMMENT ON COLUMN messages.model IS '使用的模型';
COMMENT ON COLUMN messages.create_at IS '创建时间';
COMMENT ON COLUMN messages.update_at IS '更新时间';
COMMENT ON COLUMN messages.delete_at IS '逻辑删除时间';

-- message-group-items表
CREATE TABLE message_group_items (
    id BIGINT NOT NULL,
    group_tags_id BIGINT NOT NULL,
    message_id BIGINT NOT NULL,
    sort_order INT,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_message_group_items PRIMARY KEY (id)
);

COMMENT ON TABLE message_group_items IS 'message-group-items表';
COMMENT ON COLUMN message_group_items.id IS '关联唯一ID';
COMMENT ON COLUMN message_group_items.group_tags_id IS '消息组topic ID';
COMMENT ON COLUMN message_group_items.message_id IS '消息ID';
COMMENT ON COLUMN message_group_items.sort_order IS '排序顺序';
COMMENT ON COLUMN message_group_items.create_at IS '创建时间';
COMMENT ON COLUMN message_group_items.update_at IS '更新时间';
COMMENT ON COLUMN message_group_items.delete_at IS '逻辑删除时间';

-- message-group-tags表
CREATE TABLE message_group_tags (
    id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    tag_name VARCHAR(50) NOT NULL UNIQUE,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_message_group_tags PRIMARY KEY (id)
);

COMMENT ON TABLE message_group_tags IS 'message-group-tags表';
COMMENT ON COLUMN message_group_tags.id IS '标签唯一ID';
COMMENT ON COLUMN message_group_tags.group_id IS '消息组ID';
COMMENT ON COLUMN message_group_tags.tag_name IS '标签名称';
COMMENT ON COLUMN message_group_tags.create_at IS '创建时间';
COMMENT ON COLUMN message_group_tags.update_at IS '更新时间';
COMMENT ON COLUMN message_group_tags.delete_at IS '逻辑删除时间';

-- messgae_groups表
CREATE TABLE messgae_groups (
    id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    session_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    user_id BIGINT NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_messgae_groups PRIMARY KEY (id)
);

COMMENT ON TABLE messgae_groups IS'messgae_groups表';
COMMENT ON COLUMN messgae_groups.id IS '消息组唯一ID';
COMMENT ON COLUMN messgae_groups.name IS '消息组名称';
COMMENT ON COLUMN messgae_groups.description IS '消息组描述';
COMMENT ON COLUMN messgae_groups.session_id IS '所属会话ID';
COMMENT ON COLUMN messgae_groups.is_active IS '是否活跃';
COMMENT ON COLUMN messgae_groups.user_id IS '创建人ID';
COMMENT ON COLUMN messgae_groups.create_at IS '创建时间';
COMMENT ON COLUMN messgae_groups.update_at IS '更新时间';
COMMENT ON COLUMN messgae_groups.delete_at IS '逻辑删除时间';

-- topic-relations表
CREATE TABLE topic_relations (
    id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL,
    child_id BIGINT NOT NULL,
    topic_name VARCHAR(50) NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    delete_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_topic_relations PRIMARY KEY (id)
);

COMMENT ON TABLE topic_relations IS 'topic-relations表';
COMMENT ON COLUMN topic_relations.id IS '关联唯一ID';
COMMENT ON COLUMN topic_relations.parent_id IS '父话题ID';
COMMENT ON COLUMN topic_relations.child_id IS '子话题ID';
COMMENT ON COLUMN topic_relations.topic_name IS '话题名字';
COMMENT ON COLUMN topic_relations.create_at IS '创建时间';
COMMENT ON COLUMN topic_relations.update_at IS '更新时间';
COMMENT ON COLUMN topic_relations.delete_at IS '逻辑删除时间';

-- 添加索引部分

-- files表索引
CREATE INDEX idx_user_id_files ON files (user_id);

-- users表索引
CREATE INDEX idx_user_name_users ON users (user_name);
CREATE INDEX idx_last_login_time_users ON users (last_login_time);

-- user_following表索引
CREATE INDEX idx_user_id_following ON user_following (user_id);
CREATE INDEX idx_follow_id_following ON user_following (follow_id);

-- posts表索引
CREATE INDEX idx_user_id_posts ON posts (user_id);
CREATE INDEX idx_book_id_posts ON posts (book_id);
CREATE INDEX idx_type_id_posts ON posts (type_id);

-- comments表索引
CREATE INDEX idx_user_id_comments ON comments (user_id);
CREATE INDEX idx_post_id_comments ON comments (post_id);

-- books表索引
CREATE INDEX idx_user_id_books ON books (user_id);
CREATE INDEX idx_isbn_books ON books (isbn);

-- book_underline_coordinates表索引
CREATE INDEX idx_book_id_coordinates ON book_underline_coordinates (book_id);
CREATE INDEX idx_user_id_coordinates ON book_underline_coordinates (user_id);

-- book_opinions表索引
CREATE INDEX idx_underlined_id_opinions ON book_opinions (underlined_id);

-- notebooks表索引
CREATE INDEX idx_user_id_notebooks ON notebooks (user_id);
CREATE INDEX idx_book_id_notebooks ON notebooks (book_id);

-- likes表索引
CREATE INDEX idx_user_id_likes ON likes (user_id);
CREATE INDEX idx_object_id_likes ON likes (object_id);
CREATE INDEX idx_type_likes ON likes (type);

-- favorites表索引
CREATE INDEX idx_user_id_favorites ON favorites (user_id);
CREATE INDEX idx_object_id_favorites ON favorites (object_id);
CREATE INDEX idx_type_favorites ON favorites (type);

-- agents表索引
CREATE INDEX idx_user_id_agents ON agents (user_id);

-- sessions表索引
CREATE INDEX idx_user_id_sessions ON sessions (user_id);
CREATE INDEX idx_agent_id_sessions ON sessions (agent_id);

-- messages表索引
CREATE INDEX idx_session_id_messages ON messages (session_id);

-- message_group_items表索引
CREATE INDEX idx_group_tags_id_items ON message_group_items (group_tags_id);
CREATE INDEX idx_message_id_items ON message_group_items (message_id);

-- message_group_tags表索引
CREATE INDEX idx_group_id_tags ON message_group_tags (group_id);

-- messgae_groups表索引
CREATE INDEX idx_session_id_groups ON messgae_groups (session_id);
CREATE INDEX idx_user_id_groups ON messgae_groups (user_id);

-- topic-relations表索引
CREATE INDEX idx_parent_id_relations ON topic_relations (parent_id);
CREATE INDEX idx_child_id_relations ON topic_relations (child_id);