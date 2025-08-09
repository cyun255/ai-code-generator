create database if not exists ai_code;

use ai_code;

create table if not exists user
(
    id          bigint       not null auto_increment comment 'id',
    username    varchar(16)  not null comment '用户名',
    password    varchar(64)  not null comment '密码',
    name        varchar(32)  not null comment '展示的用户昵称',
    avatar      varchar(128) null comment '用户头像超链接',
    profile     varchar(128) null comment '个人简介',
    role        tinyint      not null default 1 comment '0-admin; 1-user',
    salt        varchar(8)   not null comment '密码加盐',
    create_time datetime     not null default current_timestamp comment '创建时间',
    update_time datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete   tinyint      not null default 0 comment '是否删除',
    primary key (id),
    unique uk_username (username),
    index idx_name (name)
) comment '用户表';

create table if not exists app
(
    id            bigint       not null auto_increment comment 'id',
    name          varchar(32)  not null comment '应用名称',
    cover         varchar(128) null comment '应用封面',
    init_prompt   text         not null comment '应用初始化的 prompt',
    code_gen_type varchar(32)  not null comment '代码生成类型',
    deploy_key    varchar(8)   null comment '应用部署标识',
    deploy_time   datetime     null comment '应用部署时间',
    priority      int          not null default 0 comment '优先级',
    user_id       bigint       not null comment '创建用户 id',
    create_time   datetime     not null default current_timestamp comment '创建时间',
    update_time   datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete     tinyint      not null default 0 comment '是否删除',
    primary key (id),
    unique uk_deploy_key (deploy_key),
    index idx_name (name),
    index idx_user_id (user_id)
) comment '应用表';

create table if not exists chat_history
(
    id           bigint      not null auto_increment comment 'id',
    message      text        not null comment '消息',
    message_type varchar(32) not null comment 'ai/user',
    app_id       bigint      not null comment '应用id',
    user_id      bigint      not null comment '用户id',
    create_time  datetime    not null default current_timestamp comment '创建时间',
    update_time  datetime    not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete    tinyint     not null default 0 comment '是否删除',
    primary key (id),
    index idx_app_id (app_id),
    index idx_create_time (create_time),
    index idx_id_time (app_id, create_time)
) comment '对话历史记录表';