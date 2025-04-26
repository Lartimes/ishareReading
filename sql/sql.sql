create table files
(
    id        bigint       not null
        constraint pk_files
            primary key,
    file_path varchar(255) not null,
    file_name varchar(255) not null,
    format    varchar(50)  not null,
    type      varchar(50)  not null,
    size      bigint       not null,
    user_id   bigint       not null,
    is_public boolean default true,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

comment on table files is '文件表';

comment on column files.id is '主键';

comment on column files.file_path is '文件path';

comment on column files.file_name is '文件名字';

comment on column files.format is '文件格式';

comment on column files.type is '文件类型';

comment on column files.size is '文件大小（字节）';

comment on column files.user_id is '用户id';

comment on column files.is_public is '是否公开';

comment on column files.create_at is '创建时间';

comment on column files.update_at is '更新时间';

comment on column files.delete_at is '逻辑删除时间';

alter table files
    owner to postgres;

create table users
(
    id                 bigint       not null
        constraint pk_users
            primary key,
    email              varchar(255) not null
        unique,
    user_name          varchar(50)  not null,
    account            varchar(50)  not null,
    password           varchar(255) not null,
    self_intro         text,
    sex                varchar(10),
    avatar             varchar(255),
    reading_preference jsonb,
    last_login_time    timestamp,
    create_at          timestamp,
    update_at          timestamp,
    delete_at          timestamp
);

comment on table users is '用户表';

comment on column users.id is '主键';

comment on column users.email is '邮箱';

comment on column users.user_name is '用户名';

comment on column users.account is '账户';

comment on column users.password is '密码';

comment on column users.self_intro is '自我介绍';

comment on column users.sex is '性别';

comment on column users.avatar is '头像';

comment on column users.reading_preference is '用户阅读偏好';

comment on column users.last_login_time is '上次登录时间，用于检索';

comment on column users.create_at is '创建时间';

comment on column users.update_at is '更新时间';

comment on column users.delete_at is '逻辑删除时间';

alter table users
    owner to postgres;

create table user_following
(
    id        bigint  not null
        constraint pk_user_following
            primary key,
    user_id   bigint  not null,
    follow_id varchar not null,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp,
    type      varchar,
    type_id   bigint
);

comment on table user_following is '用户关注表';

comment on column user_following.id is '主键';

comment on column user_following.user_id is '用户id';

comment on column user_following.follow_id is '关注的用户id、帖子或者是书籍、作者等';

comment on column user_following.create_at is '创建时间';

comment on column user_following.update_at is '更新时间';

comment on column user_following.delete_at is '逻辑删除时间';

comment on column user_following.type is '关注的一大类（用户、书籍、作者、帖子类型）';

comment on column user_following.type_id is '可为空，关注的type_id';

alter table user_following
    owner to postgres;

create table posts
(
    id          bigint       not null
        constraint pk_posts
            primary key,
    user_id     bigint       not null,
    title       varchar(255) not null,
    content     text         not null,
    book_id     bigint,
    type_id     bigint,
    view_count  integer default 0,
    like_count  integer default 0,
    start_count integer default 0,
    create_at   timestamp,
    update_at   timestamp,
    delete_at   timestamp
);

comment on table posts is '帖子表';

comment on column posts.id is '主键';

comment on column posts.user_id is '用户id';

comment on column posts.title is '标题';

comment on column posts.content is '帖子内容';

comment on column posts.book_id is '关联的书籍，可为null';

comment on column posts.type_id is '帖子类型id';

comment on column posts.view_count is '浏览总数';

comment on column posts.like_count is '点赞次数';

comment on column posts.start_count is '收藏次数';

comment on column posts.create_at is '创建时间';

comment on column posts.update_at is '更新时间';

comment on column posts.delete_at is '逻辑删除时间';

alter table posts
    owner to postgres;

create table comments
(
    id               bigint not null
        constraint pk_comments
            primary key,
    user_id          bigint not null,
    post_id          bigint not null,
    reply_comment_id bigint,
    content          text   not null,
    like_count       integer default 0,
    create_at        timestamp,
    update_at        timestamp,
    delete_at        timestamp
);

comment on table comments is '评论表';

comment on column comments.id is '主键';

comment on column comments.user_id is '用户id';

comment on column comments.post_id is '帖子id';

comment on column comments.reply_comment_id is '回复评论id';

comment on column comments.content is '内容';

comment on column comments.like_count is '点赞次数';

comment on column comments.create_at is '创建时间';

comment on column comments.update_at is '更新时间';

comment on column comments.delete_at is '逻辑删除时间';

alter table comments
    owner to postgres;

create table books
(
    id               bigint       not null
        constraint pk_books
            primary key,
    name             varchar(255) not null,
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

comment on table books is '书籍表';

comment on column books.id is '主键';

comment on column books.name is '书籍name';

comment on column books.author is '书籍的作者，可能存在多位作者，可使用特定分隔符区分';

comment on column books.publication_year is '出版年份';

comment on column books.publisher is '出版社';

comment on column books.isbn is '唯一标识号';

comment on column books.genre is '书籍类型，关联type表';

comment on column books.description is '简介';

comment on column books.cover_image_id is '封面id，关联fileid';

comment on column books.total_pages is '总页数';

comment on column books.language is '语言';

comment on column books.average_rating is '评分';

comment on column books.rating_count is '用户评价总数';

comment on column books.view_count is '浏览量';

comment on column books.download_count is '下载量';

comment on column books.upload_time is '上传时间';

comment on column books.user_id is '用户id';

comment on column books.file_id is '文件id';

comment on column books.create_at is '创建时间';

comment on column books.update_at is '更新时间';

comment on column books.delete_at is '逻辑删除时间';

comment on column books.structure is '目录信息';

alter table books
    owner to postgres;

create table book_underline_coordinates
(
    id             bigint        not null
        constraint pk_book_underline_coordinates
            primary key,
    book_id        bigint        not null,
    page_number    integer       not null,
    start_x        numeric(6, 2) not null,
    start_y        numeric(6, 2) not null,
    end_x          numeric(6, 2) not null,
    end_y          numeric(6, 2) not null,
    user_id        bigint        not null,
    create_at      timestamp,
    update_at      timestamp,
    delete_at      timestamp,
    relative_xpath varchar
);

comment on table book_underline_coordinates is '书籍下环线坐标表';

comment on column book_underline_coordinates.id is '主键';

comment on column book_underline_coordinates.book_id is '书籍id';

comment on column book_underline_coordinates.page_number is '页数';

comment on column book_underline_coordinates.start_x is '起始横坐标';

comment on column book_underline_coordinates.start_y is '起始纵坐标';

comment on column book_underline_coordinates.end_x is '结束横坐标';

comment on column book_underline_coordinates.end_y is '结束纵坐标';

comment on column book_underline_coordinates.user_id is '用户id';

comment on column book_underline_coordinates.create_at is '创建时间';

comment on column book_underline_coordinates.update_at is '更新时间';

comment on column book_underline_coordinates.delete_at is '逻辑删除时间';

comment on column book_underline_coordinates.relative_xpath is '在线预览xpath';

alter table book_underline_coordinates
    owner to postgres;

create table book_opinions
(
    id            bigint not null
        constraint pk_book_opinions
            primary key,
    underlined_id bigint not null,
    opinion_text  text   not null,
    like_count    integer default 0,
    create_at     timestamp,
    update_at     timestamp,
    delete_at     timestamp
);

comment on table book_opinions is '书籍见解表';

comment on column book_opinions.id is '主键';

comment on column book_opinions.underlined_id is '下划线id';

comment on column book_opinions.opinion_text is '见解内容';

comment on column book_opinions.like_count is '点赞次数';

comment on column book_opinions.create_at is '创建时间';

comment on column book_opinions.update_at is '更新时间';

comment on column book_opinions.delete_at is '逻辑删除时间';

alter table book_opinions
    owner to postgres;

create table notebooks
(
    id           bigint not null
        constraint pk_notebooks
            primary key,
    book_id      bigint,
    user_id      bigint not null,
    note_name    varchar(255),
    note_content text,
    file_id      bigint,
    create_at    timestamp,
    update_at    timestamp,
    delete_at    timestamp
);

comment on table notebooks is 'notebook 笔记表';

comment on column notebooks.id is '主键';

comment on column notebooks.book_id is '书籍id，可为空';

comment on column notebooks.user_id is '用户id';

comment on column notebooks.note_name is '笔记名字';

comment on column notebooks.note_content is '笔记内容';

comment on column notebooks.file_id is '文件表id';

comment on column notebooks.create_at is '创建时间';

comment on column notebooks.update_at is '更新时间';

comment on column notebooks.delete_at is '逻辑删除时间';

alter table notebooks
    owner to postgres;

create table likes
(
    id        bigint      not null
        constraint pk_likes
            primary key,
    user_id   bigint      not null,
    object_id bigint      not null,
    type      varchar(50) not null,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

comment on table likes is '点赞表';

comment on column likes.id is '主键';

comment on column likes.user_id is '用户id';

comment on column likes.object_id is '目标id';

comment on column likes.type is '类型：见解、书籍、评论、帖子';

comment on column likes.create_at is '创建时间';

comment on column likes.update_at is '更新时间';

comment on column likes.delete_at is '逻辑删除时间';

alter table likes
    owner to postgres;

create table favorites
(
    id        bigint      not null
        constraint pk_favorites
            primary key,
    user_id   bigint      not null,
    object_id bigint      not null,
    type      varchar(50) not null,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

comment on table favorites is '收藏表';

comment on column favorites.id is '主键';

comment on column favorites.user_id is '用户id';

comment on column favorites.object_id is '目标id';

comment on column favorites.type is '类型：书籍、帖子';

comment on column favorites.create_at is '创建时间';

comment on column favorites.update_at is '更新时间';

comment on column favorites.delete_at is '逻辑删除时间';

alter table favorites
    owner to postgres;

create table types
(
    id        bigint      not null
        constraint pk_book_types
            primary key,
    type_name varchar(50) not null
        constraint book_types_type_name_key
            unique,
    type      varchar(50) not null,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

comment on table types is '类型表(帖子类型、书籍类型)';

comment on column types.id is '主键';

comment on column types.type_name is '名字';

comment on column types.type is '类型：书籍、帖子';

comment on column types.create_at is '创建时间';

comment on column types.update_at is '更新时间';

comment on column types.delete_at is '逻辑删除时间';

alter table types
    owner to postgres;

create table agents
(
    id                 bigint      not null
        constraint pk_agents
            primary key,
    name               varchar(50) not null,
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

comment on table agents is 'agent表';

comment on column agents.id is '主键';

comment on column agents.name is '智能体名字';

comment on column agents.avatar is '智能体头像';

comment on column agents.description is 'agent 描述';

comment on column agents.system_prompt is 'Agent 系统提示词';

comment on column agents.welcome_message is '欢迎提示语';

comment on column agents.model_config is '模型配置，包含模型类型、温度等参数';

comment on column agents.tools is 'Agent 可使用的工具列表';

comment on column agents.knowledge_base_ids is '知识库ids,json格式，配置仓库信息';

comment on column agents.agent_type is '智能体类型， 1.聊天性 2.功能型agent';

comment on column agents.user_id is '创建人用户id';

comment on column agents.create_at is '创建时间';

comment on column agents.update_at is '更新时间';

comment on column agents.delete_at is '逻辑删除时间';

alter table agents
    owner to postgres;

create table contexts
(
    id                bigint not null
        constraint pk_contexts
            primary key,
    session_id        bigint not null,
    activate_messages jsonb,
    summary           text,
    create_at         timestamp,
    update_at         timestamp,
    delete_at         timestamp
);

comment on table contexts is '上下文表';

comment on column contexts.id is '主键';

comment on column contexts.session_id is '会话id';

comment on column contexts.activate_messages is '活跃列表ids';

comment on column contexts.summary is '过去N条消息的摘要';

comment on column contexts.create_at is '创建时间';

comment on column contexts.update_at is '更新时间';

comment on column contexts.delete_at is '逻辑删除时间';

alter table contexts
    owner to postgres;

create table sessions
(
    id          bigint not null
        constraint pk_sessions
            primary key,
    title       varchar(255),
    user_id     bigint not null,
    agent_id    bigint,
    is_archived boolean default false,
    description text,
    metadata    jsonb,
    create_at   timestamp,
    update_at   timestamp,
    delete_at   timestamp
);

comment on table sessions is 'sessions表';

comment on column sessions.id is '会话唯一ID';

comment on column sessions.title is '会话标题';

comment on column sessions.user_id is '所属用户ID';

comment on column sessions.agent_id is '关联的agentId';

comment on column sessions.is_archived is '是否归档';

comment on column sessions.description is '会话描述';

comment on column sessions.metadata is '会话元数据，可存储其他自定义信息';

comment on column sessions.create_at is '创建时间';

comment on column sessions.update_at is '更新时间';

comment on column sessions.delete_at is '逻辑删除时间';

alter table sessions
    owner to postgres;

create table messages
(
    id          bigint      not null
        constraint pk_messages
            primary key,
    session_id  bigint      not null,
    role        varchar(20) not null,
    content     text        not null,
    token_count integer,
    model       varchar(50),
    create_at   timestamp,
    update_at   timestamp,
    delete_at   timestamp
);

comment on table messages is 'messages表';

comment on column messages.id is '消息唯一ID';

comment on column messages.session_id is '所属会话ID';

comment on column messages.role is '消息角色(user/assistant/system)';

comment on column messages.content is '消息内容';

comment on column messages.token_count is 'Token数量(可选，用于统计)';

comment on column messages.model is '使用的模型';

comment on column messages.create_at is '创建时间';

comment on column messages.update_at is '更新时间';

comment on column messages.delete_at is '逻辑删除时间';

alter table messages
    owner to postgres;

create table message_group_items
(
    id            bigint not null
        constraint pk_message_group_items
            primary key,
    group_tags_id bigint not null,
    message_id    bigint not null,
    sort_order    integer,
    create_at     timestamp,
    update_at     timestamp,
    delete_at     timestamp
);

comment on table message_group_items is 'message-group-items表';

comment on column message_group_items.id is '关联唯一ID';

comment on column message_group_items.group_tags_id is '消息组topic ID';

comment on column message_group_items.message_id is '消息ID';

comment on column message_group_items.sort_order is '排序顺序';

comment on column message_group_items.create_at is '创建时间';

comment on column message_group_items.update_at is '更新时间';

comment on column message_group_items.delete_at is '逻辑删除时间';

alter table message_group_items
    owner to postgres;

create table message_group_tags
(
    id        bigint      not null
        constraint pk_message_group_tags
            primary key,
    group_id  bigint      not null,
    tag_name  varchar(50) not null
        unique,
    create_at timestamp,
    update_at timestamp,
    delete_at timestamp
);

comment on table message_group_tags is 'message-group-tags表';

comment on column message_group_tags.id is '标签唯一ID';

comment on column message_group_tags.group_id is '消息组ID';

comment on column message_group_tags.tag_name is '标签名称';

comment on column message_group_tags.create_at is '创建时间';

comment on column message_group_tags.update_at is '更新时间';

comment on column message_group_tags.delete_at is '逻辑删除时间';

alter table message_group_tags
    owner to postgres;

create table messgae_groups
(
    id          bigint       not null
        constraint pk_messgae_groups
            primary key,
    name        varchar(255) not null,
    description text,
    session_id  bigint,
    is_active   boolean default true,
    user_id     bigint       not null,
    create_at   timestamp,
    update_at   timestamp,
    delete_at   timestamp
);

comment on table messgae_groups is 'messgae_groups表';

comment on column messgae_groups.id is '消息组唯一ID';

comment on column messgae_groups.name is '消息组名称';

comment on column messgae_groups.description is '消息组描述';

comment on column messgae_groups.session_id is '所属会话ID';

comment on column messgae_groups.is_active is '是否活跃';

comment on column messgae_groups.user_id is '创建人ID';

comment on column messgae_groups.create_at is '创建时间';

comment on column messgae_groups.update_at is '更新时间';

comment on column messgae_groups.delete_at is '逻辑删除时间';

alter table messgae_groups
    owner to postgres;

create table topic_relations
(
    id         bigint      not null
        constraint pk_topic_relations
            primary key,
    parent_id  bigint      not null,
    child_id   bigint      not null,
    topic_name varchar(50) not null,
    create_at  timestamp,
    update_at  timestamp,
    delete_at  timestamp
);

comment on table topic_relations is 'topic-relations表';

comment on column topic_relations.id is '关联唯一ID';

comment on column topic_relations.parent_id is '父话题ID';

comment on column topic_relations.child_id is '子话题ID';

comment on column topic_relations.topic_name is '话题名字';

comment on column topic_relations.create_at is '创建时间';

comment on column topic_relations.update_at is '更新时间';

comment on column topic_relations.delete_at is '逻辑删除时间';

alter table topic_relations
    owner to postgres;

create table book_content_page
(
    id                 bigint not null,
    book_id            bigint,
    content            text,
    "pdf_page_stream " bytea,
    page               integer,
    create_at          timestamp,
    update_at          timestamp,
    delete_at          timestamp
);

comment on column book_content_page.book_id is '关联书籍id';

comment on column book_content_page.content is '这一页的内容';

comment on column book_content_page."pdf_page_stream " is 'pdf当前页数文件流';

comment on column book_content_page.page is '当前页数（主要内容）只有大部分为文本才会记录，否则使用pdf在线预览该页';

alter table book_content_page
    owner to postgres;


