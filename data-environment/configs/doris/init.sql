SET PASSWORD = PASSWORD('123456');
alter system add BACKEND "hd1:9050";
alter system add BACKEND "hd2:9050";
alter system add BACKEND "hd3:9050";

CREATE SCHEMA de;
CREATE USER 'de' IDENTIFIED BY '123456';
GRANT ADMIN_PRIV ON *.*.* TO 'de';
