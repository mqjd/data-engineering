create TABLE `user`
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

insert into user (user_id, class_id, name, age, create_by)
select *
from (WITH RECURSIVE data_generate AS (SELECT 0 AS num
                                       UNION ALL
                                       SELECT num + 1
                                       FROM data_generate
                                       WHERE num < 10 - 1)
      SELECT num + 1                                                   as user_id,
             FLOOR(RAND() * 18)                                        as class_id,
             concat('user', '_', FLOOR(num / 5) + 1, '_', num % 5 + 1) as name,
             FLOOR(RAND() * 40) + 10                                   as age,
             'admin'                                                   as create_by
      FROM data_generate) t;

create TABLE `user_out`
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