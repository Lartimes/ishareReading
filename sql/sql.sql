-- 文件表
CREATE TABLE files
(
    id        bigint       NOT NULL
        CONSTRAINT pk_files
            PRIMARY KEY,
    file_path varchar(255) NOT NULL,
    file_name varchar(255) NOT NULL,
    format    varchar(50)  NOT NULL,
    type      varchar(50)  NOT NULL,
    size      bigint       NOT NULL,
    user_id   bigint       NOT NULL,
    is_public boolean     DEFAULT false,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
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

ALTER TABLE files
    OWNER TO postgres;

-- 用户表
CREATE TABLE users
(
    id                 bigint       NOT NULL
        CONSTRAINT pk_users
            PRIMARY KEY,
    email              varchar(255) NOT NULL UNIQUE,
    user_name          varchar(50)  NOT NULL,
    account            varchar(50)  NOT NULL,
    password           varchar(255) NOT NULL,
    self_intro         text,
    sex                varchar(10),
    avatar             varchar(255),
    reading_preference jsonb,
    last_login_time    timestamp,
    create_at          timestamp,
    update_at          timestamp,
    delete_at          timestamp
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

ALTER TABLE users
    OWNER TO postgres;

-- 用户关注表
CREATE TABLE user_following
(
    id        bigint NOT NULL
        CONSTRAINT pk_user_following
            PRIMARY KEY,
    user_id   bigint NOT NULL,
    follow_id bigint NOT NULL,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

COMMENT ON TABLE user_following IS '用户关注表';

COMMENT ON COLUMN user_following.id IS '主键';
COMMENT ON COLUMN user_following.user_id IS '用户id';
COMMENT ON COLUMN user_following.follow_id IS '关注的用户id';
COMMENT ON COLUMN user_following.create_at IS '创建时间';
COMMENT ON COLUMN user_following.update_at IS '更新时间';
COMMENT ON COLUMN user_following.delete_at IS '逻辑删除时间';

ALTER TABLE user_following
    OWNER TO postgres;

-- 帖子表
CREATE TABLE posts
(
    id          bigint       NOT NULL
        CONSTRAINT pk_posts
            PRIMARY KEY,
    user_id     bigint       NOT NULL,
    title       varchar(255) NOT NULL,
    content     text         NOT NULL,
    book_id     bigint,
    type_id     bigint,
    view_count  integer      DEFAULT 0,
    like_count  integer      DEFAULT 0,
    start_count integer      DEFAULT 0,
    create_at   timestamp,
    update_at   timestamp,
    delete_at   timestamp
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

ALTER TABLE posts
    OWNER TO postgres;

-- 评论表
CREATE TABLE comments
(
    id               bigint NOT NULL
        CONSTRAINT pk_comments
            PRIMARY KEY,
    user_id          bigint NOT NULL,
    post_id          bigint NOT NULL,
    reply_comment_id bigint,
    content          text   NOT NULL,
    like_count       integer DEFAULT 0,
    create_at        timestamp,
    update_at        timestamp,
    delete_at        timestamp
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

ALTER TABLE comments
    OWNER TO postgres;

-- 书籍表
CREATE TABLE books
(
    id               bigint       NOT NULL
        CONSTRAINT pk_books
            PRIMARY KEY,
    name             varchar(255) NOT NULL,
    author           varchar(255),
    publication_year integer,
    publisher        varchar(255),
    isbn             varchar(20),
    genre            varchar(50),
    description      text,
    cover_image_id   bigint,
    total_pages      integer,
    language         varchar(20),
    average_rating   numeric(3, 2),
    rating_count     integer,
    view_count       integer,
    download_count   integer,
    upload_time      timestamp,
    user_id          bigint,
    file_id          bigint,
    create_at        timestamp,
    update_at        timestamp,
    delete_at        timestamp,
    structure        jsonb
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
COMMENT ON COLUMN books.structure IS '目录信息';

ALTER TABLE books
    OWNER TO postgres;

-- 书籍下环线坐标表
CREATE TABLE book_underline_coordinates
(
    id             bigint        NOT NULL
        CONSTRAINT pk_book_underline_coordinates
            PRIMARY KEY,
    book_id        bigint        NOT NULL,
    page_number    integer       NOT NULL,
    start_x        numeric(6, 2) NOT NULL,
    start_y        numeric(6, 2) NOT NULL,
    end_x          numeric(6, 2) NOT NULL,
    end_y          numeric(6, 2) NOT NULL,
    user_id        bigint        NOT NULL,
    create_at      timestamp,
    update_at      timestamp,
    delete_at      timestamp,
    relative_xpath varchar
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
COMMENT ON COLUMN book_underline_coordinates.relative_xpath IS '在线预览xpath';

ALTER TABLE book_underline_coordinates
    OWNER TO postgres;

-- 书籍见解表
CREATE TABLE book_opinions
(
    id            bigint NOT NULL
        CONSTRAINT pk_book_opinions
            PRIMARY KEY,
    underlined_id bigint NOT NULL,
    opinion_text  text   NOT NULL,
    like_count    integer DEFAULT 0,
    create_at     timestamp,
    update_at     timestamp,
    delete_at     timestamp
);

COMMENT ON TABLE book_opinions IS '书籍见解表';

COMMENT ON COLUMN book_opinions.id IS '主键';
COMMENT ON COLUMN book_opinions.underlined_id IS '下划线id';
COMMENT ON COLUMN book_opinions.opinion_text IS '见解内容';
COMMENT ON COLUMN book_opinions.like_count IS '点赞次数';
COMMENT ON COLUMN book_opinions.create_at IS '创建时间';
COMMENT ON COLUMN book_opinions.update_at IS '更新时间';
COMMENT ON COLUMN book_opinions.delete_at IS '逻辑删除时间';

ALTER TABLE book_opinions
    OWNER TO postgres;

-- notebook笔记表
CREATE TABLE notebooks
(
    id           bigint NOT NULL
        CONSTRAINT pk_notebooks
            PRIMARY KEY,
    book_id      bigint,
    user_id      bigint NOT NULL,
    note_name    varchar(255),
    note_content text,
    file_id      bigint,
    create_at    timestamp,
    update_at    timestamp,
    delete_at    timestamp
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

ALTER TABLE notebooks
    OWNER TO postgres;

-- 点赞表
CREATE TABLE likes
(
    id        bigint      NOT NULL
        CONSTRAINT pk_likes
            PRIMARY KEY,
    user_id   bigint      NOT NULL,
    object_id bigint      NOT NULL,
    type      varchar(50) NOT NULL,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

COMMENT ON TABLE likes IS '点赞表';

COMMENT ON COLUMN likes.id IS '主键';
COMMENT ON COLUMN likes.user_id IS '用户id';
COMMENT ON COLUMN likes.object_id IS '目标id';
COMMENT ON COLUMN likes.type IS '类型：见解、书籍、评论、帖子';
COMMENT ON COLUMN likes.create_at IS '创建时间';
COMMENT ON COLUMN likes.update_at IS '更新时间';
COMMENT ON COLUMN likes.delete_at IS '逻辑删除时间';

ALTER TABLE likes
    OWNER TO postgres;

-- 收藏表
CREATE TABLE favorites
(
    id        bigint      NOT NULL
        CONSTRAINT pk_favorites
            PRIMARY KEY,
    user_id   bigint      NOT NULL,
    object_id bigint      NOT NULL,
    type      varchar(50) NOT NULL,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

COMMENT ON TABLE favorites IS '收藏表';

COMMENT ON COLUMN favorites.id IS '主键';
COMMENT ON COLUMN favorites.user_id IS '用户id';
COMMENT ON COLUMN favorites.object_id IS '目标id';
COMMENT ON COLUMN favorites.type IS '类型：书籍、帖子';
COMMENT ON COLUMN favorites.create_at IS '创建时间';
COMMENT ON COLUMN favorites.update_at IS '更新时间';
COMMENT ON COLUMN favorites.delete_at IS '逻辑删除时间';

ALTER TABLE favorites
    OWNER TO postgres;

-- 书籍类型表
CREATE TABLE book_types
(
    id        bigint      NOT NULL
        CONSTRAINT pk_book_types
            PRIMARY KEY,
    type_name varchar(50) NOT NULL UNIQUE,
    type      varchar(50) NOT NULL,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

COMMENT ON TABLE book_types IS '书籍类型表';

COMMENT ON COLUMN book_types.id IS '主键';
COMMENT ON COLUMN book_types.type_name IS '名字';
COMMENT ON COLUMN book_types.type IS '类型：书籍、帖子';
COMMENT ON COLUMN book_types.create_at IS '创建时间';
COMMENT ON COLUMN book_types.update_at IS '更新时间';
COMMENT ON COLUMN book_types.delete_at IS '逻辑删除时间';

ALTER TABLE book_types
    OWNER TO postgres;

-- agent表
CREATE TABLE agents
(
    id                 bigint      NOT NULL
        CONSTRAINT pk_agents
            PRIMARY KEY,
    name               varchar(50) NOT NULL,
    avatar             varchar(255),
    description        text,
    system_prompt      text,
    welcome_message    text,
    model_config       jsonb,
    tools              jsonb,
    knowledge_base_ids jsonb,
    agent_type         integer,
    user_id            bigint,
    create_at          timestamp,
    update_at          timestamp,
    delete_at          timestamp
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

ALTER TABLE agents
    OWNER TO postgres;

-- 上下文表
CREATE TABLE contexts
(
    id                bigint NOT NULL
        CONSTRAINT pk_contexts
            PRIMARY KEY,
    session_id        bigint NOT NULL,
    activate_messages jsonb,
    summary           text,
    create_at         timestamp,
    update_at         timestamp,
    delete_at         timestamp
);

COMMENT ON TABLE contexts IS '上下文表';

COMMENT ON COLUMN contexts.id IS '主键';
COMMENT ON COLUMN contexts.session_id IS '会话id';
COMMENT ON COLUMN contexts.activate_messages IS '活跃列表ids';
COMMENT ON COLUMN contexts.summary IS '过去N条消息的摘要';
COMMENT ON COLUMN contexts.create_at IS '创建时间';
COMMENT ON COLUMN contexts.update_at IS '更新时间';
COMMENT ON COLUMN contexts.delete_at IS '逻辑删除时间';

ALTER TABLE contexts
    OWNER TO postgres;

-- sessions表
CREATE TABLE sessions
(
    id          bigint NOT NULL
        CONSTRAINT pk_sessions
            PRIMARY KEY,
    title       varchar(255),
    user_id     bigint NOT NULL,
    agent_id    bigint,
    is_archived boolean DEFAULT false,
    description text,
    metadata    jsonb,
    create_at   timestamp,
    update_at