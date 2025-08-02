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