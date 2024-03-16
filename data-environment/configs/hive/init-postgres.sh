#!/usr/bin/env bash

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER hive WITH PASSWORD '123456';
  CREATE DATABASE metastore OWNER hive;
  GRANT ALL PRIVILEGES ON DATABASE metastore to hive;
EOSQL