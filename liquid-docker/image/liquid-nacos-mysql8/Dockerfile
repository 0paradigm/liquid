FROM mysql:8.0.30

ADD sql /usr/local/sql
ADD initdb.sql /docker-entrypoint-initdb.d/initdb.sql

RUN chown -R mysql:mysql /docker-entrypoint-initdb.d/initdb.sql

EXPOSE 3306
CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]
