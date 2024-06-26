-- ----------------------------------------------------------------------------------------------------------------
-- DATABASE:  chapter2_section3_target
-- ----------------------------------------------------------------------------------------------------------------
drop table if exists `user_target`;
create TABLE `user_target`
(
    `id`          int(11)     NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`     varchar(10) NOT NULL COMMENT '用户ID',
    `class_id`    int(11)     NOT NULL COMMENT '班级ID',
    `name`        varchar(50) NULL DEFAULT NULL COMMENT '姓名',
    `age`         tinyint     NULL COMMENT '年龄',
    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON update CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `create_by`   varchar(20) NULL DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`) USING BTREE
) COMMENT = '用户表';