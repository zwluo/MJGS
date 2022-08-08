-- drop database test1;
-- 创建数据库
create database test1 DEFAULT CHARACTER SET utf8;

use test1;
-- 故事表
-- drop table tale;
create table tale
(
    id         Int primary key auto_increment comment '编号',
    type      varchar(50) comment '栏目分类',
    column_id int comment '目录分类id',
    title       varchar(50) comment '标题',
    content    text comment '内容',
    narrator   varchar(20) comment '口述人',
    recorder    varchar(20) comment '搜集人',
    create_date TIMESTAMP comment '创建时间',
    create_by   varchar(20) comment '创建人'
);
create index idx_tale on tale(column_id);

-- 访问日志表
-- drop table visit_log
create table visit_log
(
    id         Int primary key auto_increment comment '编号',
    method      varchar(50) comment '处理方法',
    request      varchar(50) comment '请求参数',
    response       varchar(50) comment '接口返回',
    ip      varchar(50) comment '访问者IP',
    useragent varchar(1000) comment '访问者设备信息',
    create_date TIMESTAMP comment '创建时间' default now(),
    create_by   varchar(20) comment '创建人' default 'system'
);

-- 反馈表
create table feedback
(
    id         Int primary key auto_increment comment '编号',
    type      varchar(50) comment '栏目分类',
    title       varchar(50) comment '标题',
    content    text comment '反馈内容',
    ip      varchar(50) comment '访问者IP',
    useragent varchar(1000) comment '访问者设备信息',
    create_date TIMESTAMP comment '创建时间' default now(),
    create_by   varchar(20) comment '创建人' default 'system'
);

-- 书籍表
create table book
(
    id         Int primary key auto_increment comment '编号',
    title       varchar(50) comment '书名',
    url   varchar(100) comment '书籍链接',
    cover    varchar(100) comment '封面图片url',
    create_date TIMESTAMP comment '创建时间',
    create_by   varchar(20) comment '创建人'
);

-- 书籍目录分类表
create table book_column
(
    id         Int primary key auto_increment comment '编号',
    title       varchar(50) comment '目录分类名称',
    book_id   Int comment '书籍Id',
    create_date TIMESTAMP comment '创建时间',
    create_by   varchar(20) comment '创建人'
);
create index idx_book_column on book_column(book_id);
