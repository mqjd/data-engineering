CREATE USER 'mq'@'%' IDENTIFIED BY '123456';
CREATE SCHEMA mq COLLATE utf8mb4_general_ci;
GRANT ALL ON mq.* TO 'mq'@'%';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT, LOCK TABLES  ON *.* TO 'mq'@'%';